const fs = require("fs");
const path = require("path");

const sourceDirectory = "C:/Users/cpalm/Downloads";
const eggBlockTexture = "C:/Users/cpalm/.cursor/projects/c-Code-msio-random/assets/c__Users_cpalm_AppData_Roaming_Cursor_User_workspaceStorage_107636cae42076f2cafc70aee4d787cb_images_cragbeast_egg_north__1_-eb28c1a7-4d3c-4255-8d5f-59126c7a5d7d.png";
const model = JSON.parse(fs.readFileSync(path.join(sourceDirectory, "cragbeast_godzilla_sized_geckolib.bbmodel"), "utf8"));
const assetsDirectory = path.join(__dirname, "..", "src", "main", "resources", "assets", "minesharesiorandom");

const groups = new Map(model.groups.map((group) => [group.uuid, group]));
const elements = new Map(model.elements.map((element) => [element.uuid, element]));

function convertFaces(faces) {
    return Object.fromEntries(Object.entries(faces).map(([direction, face]) => {
        const [u1, v1, u2, v2] = face.uv;
        return [direction, { uv: [u1, v1], uv_size: [u2 - u1, v2 - v1] }];
    }));
}

function convertBone(node, parent) {
    const group = groups.get(node.uuid);
    const bone = {
        name: group.name,
        pivot: group.origin,
        cubes: []
    };

    if (parent) {
        bone.parent = parent;
    }

    if (group.rotation.some((value) => value !== 0)) {
        bone.rotation = group.rotation;
    }

    for (const child of node.children) {
        if (typeof child === "string") {
            const element = elements.get(child);
            if (!element || !element.export) continue;

            bone.cubes.push({
                origin: element.from,
                size: element.to.map((value, index) => value - element.from[index]),
                pivot: element.origin,
                uv: convertFaces(element.faces)
            });
        }
    }

    const bones = group.export ? [bone] : [];
    for (const child of node.children) {
        if (typeof child !== "string") {
            bones.push(...convertBone(child, group.export ? group.name : parent));
        }
    }

    return bones;
}

const geometry = {
    format_version: "1.12.0",
    "minecraft:geometry": [{
        description: {
            identifier: "geometry.cragbeast",
            texture_width: model.resolution.width,
            texture_height: model.resolution.height,
            visible_bounds_width: 128,
            visible_bounds_height: 64,
            visible_bounds_offset: [0, 24, 0]
        },
        bones: model.outliner.flatMap((node) => convertBone(node))
    }]
};

const babyGeometry = JSON.parse(JSON.stringify(geometry));
const babyScale = 0.35;
babyGeometry["minecraft:geometry"][0].description.identifier = "geometry.cragbeast_baby";
babyGeometry["minecraft:geometry"][0].description.visible_bounds_width *= babyScale;
babyGeometry["minecraft:geometry"][0].description.visible_bounds_height *= babyScale;
babyGeometry["minecraft:geometry"][0].description.visible_bounds_offset = babyGeometry["minecraft:geometry"][0].description.visible_bounds_offset
    .map((value) => value * babyScale);
for (const bone of babyGeometry["minecraft:geometry"][0].bones) {
    bone.pivot = bone.pivot.map((value) => value * babyScale);
    for (const cube of bone.cubes) {
        cube.origin = cube.origin.map((value) => value * babyScale);
        cube.size = cube.size.map((value) => value * babyScale);
        cube.pivot = cube.pivot.map((value) => value * babyScale);
    }
}

fs.mkdirSync(path.join(assetsDirectory, "geo"), { recursive: true });
fs.mkdirSync(path.join(assetsDirectory, "animations"), { recursive: true });
fs.mkdirSync(path.join(assetsDirectory, "textures", "entity"), { recursive: true });
fs.mkdirSync(path.join(assetsDirectory, "textures", "item"), { recursive: true });
fs.mkdirSync(path.join(assetsDirectory, "textures", "block"), { recursive: true });
fs.writeFileSync(path.join(assetsDirectory, "geo", "cragbeast.geo.json"), JSON.stringify(geometry, null, 2));
fs.writeFileSync(path.join(assetsDirectory, "geo", "cragbeast_baby.geo.json"), JSON.stringify(babyGeometry, null, 2));
fs.copyFileSync(path.join(sourceDirectory, "cragbeast_godzilla_sized_geckolib.json"), path.join(assetsDirectory, "animations", "cragbeast.animation.json"));
fs.copyFileSync(path.join(sourceDirectory, "cragbeast_godzilla_sized_geckolib_baby.json"), path.join(assetsDirectory, "animations", "cragbeast_baby.animation.json"));
fs.copyFileSync(path.join(sourceDirectory, "cragbeast_godzilla_sized_geckolib.png"), path.join(assetsDirectory, "textures", "entity", "cragbeast.png"));
fs.copyFileSync(path.join(sourceDirectory, "cragbeast_baby.png"), path.join(assetsDirectory, "textures", "entity", "cragbeast_baby.png"));
fs.copyFileSync(path.join(sourceDirectory, "cragbeast_spawn_egg.png"), path.join(assetsDirectory, "textures", "item", "cragbeast_spawn_egg.png"));
fs.copyFileSync(eggBlockTexture, path.join(assetsDirectory, "textures", "block", "cragbeast_egg.png"));

const fs = require("fs");
const path = require("path");

const sourceModel = "C:/Users/cpalm/Downloads/hatzegopteryx_v3.bbmodel";
const spawnEggTextureSource = "C:/Users/cpalm/Downloads/hatzegopteryx_texture.png";
const assetsDirectory = path.join(__dirname, "..", "src", "main", "resources", "assets", "minesharesiorandom");

const model = JSON.parse(fs.readFileSync(sourceModel, "utf8"));
const elements = new Map(model.elements.map((element) => [element.uuid, element]));

const TARGET_HEIGHT_BLOCKS = 4.6;
const modelHeightUnits = model.elements.reduce((maxY, element) => Math.max(maxY, element.to[1]), 0)
    - model.elements.reduce((minY, element) => Math.min(minY, element.from[1]), Infinity);
const modelWidthUnits = model.elements.reduce((maxX, element) => Math.max(maxX, element.to[0]), -Infinity)
    - model.elements.reduce((minX, element) => Math.min(minX, element.from[0]), Infinity);
const heightScale = TARGET_HEIGHT_BLOCKS / (modelHeightUnits / 16);

function convertFaces(faces) {
    return Object.fromEntries(
        Object.entries(faces).map(([direction, face]) => {
            const [u1, v1, u2, v2] = face.uv;
            return [direction, { uv: [u1, v1], uv_size: [u2 - u1, v2 - v1] }];
        })
    );
}

function collectBoneNames(node, map) {
    if (node.uuid && node.name) {
        map.set(node.uuid, node.name);
    }
    for (const child of node.children || []) {
        if (typeof child === "object") {
            collectBoneNames(child, map);
        }
    }
}

function convertBone(node, parent) {
    const bone = {
        name: node.name,
        pivot: node.origin || [0, 0, 0],
        cubes: []
    };

    if (parent) {
        bone.parent = parent;
    }

    if (node.rotation && node.rotation.some((value) => value !== 0)) {
        bone.rotation = node.rotation;
    }

    for (const child of node.children || []) {
        if (typeof child !== "string") {
            continue;
        }

        const element = elements.get(child);
        if (!element || element.export === false) {
            continue;
        }

        const cube = {
            origin: element.from,
            size: element.to.map((value, index) => value - element.from[index]),
            pivot: element.origin,
            uv: convertFaces(element.faces)
        };

        if (element.rotation && element.rotation.some((value) => value !== 0)) {
            cube.rotation = element.rotation;
        }

        bone.cubes.push(cube);
    }

    const bones = node.export !== false ? [bone] : [];
    const childParent = node.export !== false ? node.name : parent;

    for (const child of node.children || []) {
        if (typeof child === "object") {
            bones.push(...convertBone(child, childParent));
        }
    }

    return bones;
}

const uuidToName = new Map();
for (const root of model.outliner) {
    collectBoneNames(root, uuidToName);
}

const geometry = {
    format_version: "1.12.0",
    "minecraft:geometry": [
        {
            description: {
                identifier: "geometry.hatzegopteryx",
                texture_width: model.resolution.width,
                texture_height: model.resolution.height,
                visible_bounds_width: (modelWidthUnits / 16) * heightScale,
                visible_bounds_height: TARGET_HEIGHT_BLOCKS,
                visible_bounds_offset: [0, TARGET_HEIGHT_BLOCKS / 2, 0]
            },
            bones: model.outliner.flatMap((node) => convertBone(node))
        }
    ]
};

function formatTime(time) {
    return Number.isInteger(time) ? String(time) : String(time);
}

function convertAnimations() {
    const animations = {};

    for (const animation of model.animations || []) {
        const bones = {};

        for (const [boneUuid, boneAnimation] of Object.entries(animation.bones || {})) {
            const boneName = uuidToName.get(boneUuid) || boneAnimation.name;
            if (!boneName) {
                continue;
            }

            const channels = {};

            for (const keyframe of boneAnimation.keyframes || []) {
                const channel = keyframe.channel;
                if (channel !== "rotation" && channel !== "position") {
                    continue;
                }

                if (!channels[channel]) {
                    channels[channel] = {};
                }

                const point = keyframe.data_points[0];
                channels[channel][formatTime(keyframe.time)] = {
                    vector: [Number(point.x), Number(point.y), Number(point.z)]
                };
            }

            if (Object.keys(channels).length > 0) {
                bones[boneName] = channels;
            }
        }

        animations[animation.name] = {
            loop: animation.loop === "loop",
            animation_length: animation.length,
            bones
        };
    }

    return {
        format_version: "1.8.0",
        animations
    };
}

function writeTexture() {
    const texture = model.textures[0];
    const outputPath = path.join(assetsDirectory, "textures", "entity", "hatzegopteryx.png");

    if (texture.source && texture.source.startsWith("data:image/png;base64,")) {
        const base64 = texture.source.slice("data:image/png;base64,".length);
        fs.writeFileSync(outputPath, Buffer.from(base64, "base64"));
        return;
    }

    if (texture.path && fs.existsSync(texture.path)) {
        fs.copyFileSync(texture.path, outputPath);
        return;
    }

    const sibling = sourceModel.replace(/\.bbmodel$/, ".png");
    if (fs.existsSync(sibling)) {
        fs.copyFileSync(sibling, outputPath);
    }
}

fs.mkdirSync(path.join(assetsDirectory, "geo"), { recursive: true });
fs.mkdirSync(path.join(assetsDirectory, "animations"), { recursive: true });
fs.mkdirSync(path.join(assetsDirectory, "textures", "entity"), { recursive: true });
fs.mkdirSync(path.join(assetsDirectory, "textures", "item"), { recursive: true });

fs.writeFileSync(path.join(assetsDirectory, "geo", "hatzegopteryx.geo.json"), JSON.stringify(geometry, null, 2));
fs.writeFileSync(
    path.join(assetsDirectory, "animations", "hatzegopteryx.animation.json"),
    JSON.stringify(convertAnimations(), null, 2)
);
writeTexture();

const spawnEggTexture = path.join(assetsDirectory, "textures", "item", "hatzegopteryx_spawn_egg.png");
if (fs.existsSync(spawnEggTextureSource)) {
    fs.copyFileSync(spawnEggTextureSource, spawnEggTexture);
}

console.log("Exported Hatzegopteryx assets.");

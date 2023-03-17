package dev.uten2c.raincoat.model;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public record GunModel(
        List<ModelElement> elements,
        Map<String, Either<SpriteIdentifier, String>> textures,
        Map<Integer, List<Integer>> removeIndices,
        Map<Integer, ModelTransformation> modelTransformations
) {
    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;

    public byte[] toByteArray() throws IOException {
        try (final var byteArrayOutputStream = new ByteArrayOutputStream()) {
            final var out = new DataOutputStream(byteArrayOutputStream);
            writeList(out, elements, GunModel::writeModelElement);
            writeMap(out, textures, GunModel::writeString, GunModel::writeTexture);
            writeMap(out, removeIndices, GunModel::writeVarInt, (o, l) -> writeList(o, l, GunModel::writeVarInt));
            writeMap(out, modelTransformations, GunModel::writeVarInt, GunModel::writeModelTransformation);
            return byteArrayOutputStream.toByteArray();
        }
    }

    public static @NotNull GunModel fromByteArray(byte[] bytes) throws IOException {
        try (final var in = new DataInputStream(new ByteArrayInputStream(bytes))) {
            final var elements = readList(in, GunModel::readModelElement);
            final var textures = readMap(in, GunModel::readString, GunModel::readTexture);
            final var removeIndices = readMap(in, GunModel::readVarInt, i -> readList(i, GunModel::readVarInt));
            final var modelTransformations = readMap(in, GunModel::readVarInt, GunModel::readModelTransformation);
            return new GunModel(elements, textures, removeIndices, modelTransformations);
        }
    }

    private static @NotNull ModelElement readModelElement(@NotNull DataInputStream in) {
        final var from = readVector3f(in);
        final var to = readVector3f(in);
        final var faces = readMap(in, i -> readEnum(i, Direction.class), GunModel::readModelElementFace);
        final var rotation = readNullable(in, GunModel::readModelRotation);
        final var shade = readBoolean(in);
        return new ModelElement(from, to, faces, rotation, shade);
    }

    private static void writeModelElement(@NotNull DataOutputStream out, @NotNull ModelElement value) {
        writeVector3f(out, value.from);
        writeVector3f(out, value.to);
        writeMap(out, value.faces, GunModel::writeEnum, GunModel::writeModelElementFace);
        writeNullable(out, value.rotation, GunModel::writeModelRotation);
        writeBoolean(out, value.shade);
    }

    private static @NotNull ModelElementFace readModelElementFace(@NotNull DataInputStream in) {
        final var direction = readNullable(in, i -> readEnum(i, Direction.class));
        final var tintIndex = readVarInt(in);
        final var texture = readString(in);
        final var textureData = readModelElementTexture(in);
        return new ModelElementFace(direction, tintIndex, texture, textureData);
    }

    private static void writeModelElementFace(@NotNull DataOutputStream out, @NotNull ModelElementFace value) {
        writeNullable(out, value.cullFace, GunModel::writeEnum);
        writeVarInt(out, value.tintIndex);
        writeString(out, value.textureId);
        writeModelElementTexture(out, value.textureData);
    }

    private static @NotNull ModelRotation readModelRotation(@NotNull DataInputStream in) {
        final var angle = readFloat(in);
        final var axis = readEnum(in, Direction.Axis.class);
        final var origin = readVector3f(in);
        final var rescale = readBoolean(in);
        return new ModelRotation(origin.mul(0.0625f), axis, angle, rescale);
    }

    private static void writeModelRotation(@NotNull DataOutputStream out, @NotNull ModelRotation value) {
        writeFloat(out, value.angle());
        writeEnum(out, value.axis());
        writeVector3f(out, value.origin());
        writeBoolean(out, value.rescale());
    }

    private static @NotNull ModelElementTexture readModelElementTexture(@NotNull DataInputStream in) {
        return new ModelElementTexture(readNullable(in, GunModel::readUv), readVarInt(in));
    }

    private static void writeModelElementTexture(@NotNull DataOutputStream out, @NotNull ModelElementTexture value) {
        writeNullable(out, value.uvs, GunModel::writeUv);
        writeVarInt(out, value.rotation);
    }

    private static float @NotNull [] readUv(@NotNull DataInputStream in) {
        return new float[]{readFloat(in), readFloat(in), readFloat(in), readFloat(in)};
    }

    private static void writeUv(@NotNull DataOutputStream out, float[] uv) {
        writeFloat(out, uv[0]);
        writeFloat(out, uv[1]);
        writeFloat(out, uv[2]);
        writeFloat(out, uv[3]);
    }

    private static @NotNull ModelTransformation readModelTransformation(@NotNull DataInputStream in) {
        return new ModelTransformation(
                readDisplay(in),
                readDisplay(in),
                readDisplay(in),
                readDisplay(in),
                readDisplay(in),
                readDisplay(in),
                readDisplay(in),
                readDisplay(in)
        );
    }

    private static void writeModelTransformation(@NotNull DataOutputStream out, @NotNull ModelTransformation value) {
        writeDisplay(out, value.thirdPersonLeftHand);
        writeDisplay(out, value.thirdPersonRightHand);
        writeDisplay(out, value.firstPersonLeftHand);
        writeDisplay(out, value.firstPersonRightHand);
        writeDisplay(out, value.head);
        writeDisplay(out, value.gui);
        writeDisplay(out, value.ground);
        writeDisplay(out, value.fixed);
    }

    private static @NotNull Transformation readDisplay(@NotNull DataInputStream in) {
        if (readBoolean(in)) {
            return new Transformation(readVector3f(in), readVector3f(in).mul(0.0625f), readVector3f(in));
        } else {
            return Transformation.IDENTITY;
        }
    }

    private static void writeDisplay(@NotNull DataOutputStream out, @NotNull Transformation value) {
        if (value.equals(Transformation.IDENTITY)) {
            writeBoolean(out, false);
        } else {
            writeBoolean(out, true);
            writeVector3f(out, value.rotation);
            writeVector3f(out, value.translation);
            writeVector3f(out, value.scale);
        }
    }

    private static @NotNull Either<SpriteIdentifier, String> readTexture(@NotNull DataInputStream in) {
        if (readBoolean(in)) {
            return Either.left(new SpriteIdentifier(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, new Identifier(readString(in))));
        } else {
            return Either.right(readString(in));
        }
    }

    private static void writeTexture(@NotNull DataOutputStream out, @NotNull Either<SpriteIdentifier, String> value) {
        value.ifLeft(id -> {
            writeBoolean(out, true);
            writeString(out, id.getTextureId().toString());
        }).ifRight(ref -> {
            writeBoolean(out, false);
            writeString(out, ref);
        });
    }

    private static @NotNull Vector3f readVector3f(@NotNull DataInputStream in) {
        return new Vector3f(readFloat(in), readFloat(in), readFloat(in));
    }

    private static void writeVector3f(@NotNull DataOutputStream out, @NotNull Vector3f vector3f) {
        writeFloat(out, vector3f.x());
        writeFloat(out, vector3f.y());
        writeFloat(out, vector3f.z());
    }

    private static <K, V> @NotNull Map<K, V> readMap(@NotNull DataInputStream in, @NotNull Function<DataInputStream, K> keyReader, @NotNull Function<DataInputStream, V> valueReader) {
        final var size = readVarInt(in);
        final var map = new HashMap<K, V>(size);
        for (int i = 0; i < size; i++) {
            map.put(keyReader.apply(in), valueReader.apply(in));
        }
        return map;
    }

    private static <K, V> void writeMap(@NotNull DataOutputStream out, Map<K, V> value, @NotNull BiConsumer<DataOutputStream, K> keyWriter, @NotNull BiConsumer<DataOutputStream, V> valueWriter) {
        writeVarInt(out, value.size());
        for (Map.Entry<K, V> entry : value.entrySet()) {
            keyWriter.accept(out, entry.getKey());
            valueWriter.accept(out, entry.getValue());
        }
    }

    private static <T> @NotNull List<T> readList(@NotNull DataInputStream in, @NotNull Function<DataInputStream, T> reader) {
        final var size = readVarInt(in);
        final var list = new ArrayList<T>(size);
        for (int i = 0; i < size; i++) {
            list.add(reader.apply(in));
        }
        return list;
    }

    private static <T> void writeList(@NotNull DataOutputStream out, List<T> value, @NotNull BiConsumer<DataOutputStream, T> writer) {
        writeVarInt(out, value.size());
        for (T item : value) {
            writer.accept(out, item);
        }
    }

    private static <T> @Nullable T readNullable(@NotNull DataInputStream in, @NotNull Function<DataInputStream, T> reader) {
        if (readBoolean(in)) {
            return reader.apply(in);
        } else {
            return null;
        }
    }

    private static <T> void writeNullable(@NotNull DataOutputStream out, @Nullable T value, @NotNull BiConsumer<DataOutputStream, T> writer) {
        if (value == null) {
            writeBoolean(out, false);
        } else {
            writeBoolean(out, true);
            writer.accept(out, value);
        }
    }

    private static boolean readBoolean(@NotNull DataInputStream in) {
        try {
            return in.readBoolean();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeBoolean(@NotNull DataOutputStream out, boolean value) {
        try {
            out.writeBoolean(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static float readFloat(@NotNull DataInputStream in) {
        try {
            return in.readFloat();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeFloat(@NotNull DataOutputStream out, float value) {
        try {
            out.writeFloat(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int readVarInt(@NotNull DataInputStream in) {
        try {
            int value = 0;
            int position = 0;
            byte currentByte;

            while (true) {
                currentByte = in.readByte();
                value |= (currentByte & SEGMENT_BITS) << position;

                if ((currentByte & CONTINUE_BIT) == 0) break;

                position += 7;

                if (position >= 32) throw new RuntimeException("VarInt is too big");
            }

            return value;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeVarInt(@NotNull DataOutputStream out, int value) {
        try {
            while (true) {
                if ((value & ~SEGMENT_BITS) == 0) {
                    out.writeByte(value);
                    return;
                }
                out.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
                value >>>= 7;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T extends Enum<T>> @NotNull T readEnum(@NotNull DataInputStream in, @NotNull Class<T> enumClass) {
        return enumClass.getEnumConstants()[readVarInt(in)];
    }

    private static <T extends Enum<T>> void writeEnum(@NotNull DataOutputStream out, @NotNull Enum<T> value) {
        writeVarInt(out, value.ordinal());
    }

    private static @NotNull String readString(@NotNull DataInputStream in) {
        try {
            return in.readUTF();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeString(@NotNull DataOutputStream out, @NotNull String value) {
        try {
            out.writeUTF(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

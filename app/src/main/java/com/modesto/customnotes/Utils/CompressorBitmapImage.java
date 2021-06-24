package com.modesto.customnotes.Utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;


//Esta clase nos permitira comprimir una imagen los valores son estandares ueden ser cambiados pero asi nos establecen una buena calidad y un buen peso asi
//que es mejor dejarlos asi
public class CompressorBitmapImage {

    /*
     * Metodo que permite comprimir imagenes y transformarlas a bitmap
     */
    public static byte[] getImage(Context ctx, String path, int width, int height) {
        final File file_thumb_path = new File(path);
        Bitmap thumb_bitmap = null;

        try {
            thumb_bitmap = new Compressor(ctx)
                    .setMaxWidth(width)
                    .setMaxHeight(height)
                    //Este 75 es la calidad mientras mas alto sea mas pesado estara la imagen
                    .setQuality(75)
                    .compressToBitmap(file_thumb_path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,80,baos);
        byte[] thumb_byte = baos.toByteArray();
        return  thumb_byte;
    }
}

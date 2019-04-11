package com.example.kgsgeogame;

import android.widget.Toast;

import java.io.File;
import java.util.Vector;

class PhotoAlbum {
    private static Vector <File> album = new Vector<>();
    private static int picCount = 0;


    static void store(File fIn){
        album.add(fIn);
        picCount++;
    }
}

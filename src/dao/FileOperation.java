package dao;

import jdk.nashorn.internal.runtime.ECMAException;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by hp on 2015/12/20.
 */
public class FileOperation {

    public static boolean Delete(String delpath) {
        try {
            File file = new File(delpath);

            if (!file.isDirectory()){
                file.delete();
            }else if (file.isDirectory()){
                String[] filelist = file.list();
                for (int i = 0; i < filelist.length; i++){
                    File delfile = new File(delpath + "\\" + filelist[i]);
                    if (!delfile.isDirectory()){
                        delfile.delete();
                    } else if (delfile.isDirectory()){
                        Delete(delpath + "\\" + filelist[i]);
                    }
                }
                file.delete();
            }
        } catch (Exception e) {
            System.out.println("deletefile() Exception:" + e.getMessage());
            return false;
        }
        return true;
    }


}

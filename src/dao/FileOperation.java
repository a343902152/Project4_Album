package dao;

import java.io.File;

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
                    File delfile = new File(delpath + "/" + filelist[i]);
                    if (!delfile.isDirectory()){
                        delfile.delete();
                    } else if (delfile.isDirectory()){
                        Delete(delpath + "/" + filelist[i]);
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

    public static boolean Rename(String url,String newurl){
        File file=new File(url);
        if(file.exists()){
            file.renameTo(new File(newurl));
        }else{
            return false;
        }
        return true;
    }

    public static boolean Mkdir(String url){
        File file = new File(url);
        //判断上传文件的保存目录是否存在
        if (!file.exists() || !file.isDirectory()) {
            System.out.println(url+"目录不存在，需要创建");
            //创建目录
            file.mkdir();
        }
        return true;
    }

}

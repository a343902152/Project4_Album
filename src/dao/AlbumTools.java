package dao;

import JavaBean.Album;
import JavaBean.Photo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hp on 2015/12/14.
 * 根据userid获取albumlist
 * 根据albumid获取photolist
 */
public class AlbumTools {

    private static Connection dbConn= MyDatabase.getDBConnection();

    /**
     * 根据用户id，获取他拥有的相册list
     * @param userid
     * @return
     */
    public static List<Album> getAlbumList(String userid){
        String sql="select * from albums where userid=?";
        List<Album> list=new ArrayList<Album>();
        try{
            PreparedStatement preparedStatement=dbConn.prepareStatement(sql);
            preparedStatement.setString(1,userid);
            ResultSet resultSet=preparedStatement.executeQuery();
            while(resultSet.next()){
                Album album=new Album(resultSet.getString(2),
                        resultSet.getString(3),resultSet.getString(4));
                list.add(album);
            }
            preparedStatement.close();

            String sqlPhoto = "select * from photos where albumid=?";
            for (int i = 0; i < list.size(); i++) {
                PreparedStatement tempPST = dbConn.prepareStatement(sqlPhoto);
                tempPST.setString(1, list.get(i).getId());
                ResultSet tempres = tempPST.executeQuery();
                if (tempres.next()) {
                    Photo photo = new Photo(tempres.getString(1), tempres.getString(2),
                            tempres.getString(3), tempres.getString(4), tempres.getString(5));
                    list.get(i).setFirstImgUrl(photo.getUrl());
                    System.out.println(list.get(i).getFirstImgUrl());
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return list;
    }


    public static List<Photo> getPhotoList(String albumid){
        String sql="select * from photos where albumid=?";
        PreparedStatement preparedStatement;
        List<Photo> list=new ArrayList<Photo>();
        try{
            preparedStatement=dbConn.prepareStatement(sql);
            preparedStatement.setString(1,albumid);

            ResultSet resultSet=preparedStatement.executeQuery();
            while(resultSet.next()){
                Photo photo=new Photo(resultSet.getString(1),resultSet.getString(2),
                        resultSet.getString(3),resultSet.getString(4),resultSet.getString(5));
                list.add(photo);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return list;
    }


}

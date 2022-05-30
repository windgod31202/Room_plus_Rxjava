package com.example.room_test001.database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;
import java.util.Observable;

import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

@Dao
public interface DataUao {

    String tableName = "MyTable01";
    /**=======================================================================================*/
    /**簡易新增所有資料的方法*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)//預設萬一執行出錯，REPLACE為覆蓋
    void insertData(MyData myData);

    /**複雜(?)新增所有資料的方法*/
    @Query("INSERT INTO "+tableName+"(name,Age,Hobby,Email) VALUES(:name,:Age,:Hobby,:Email)")
    void insertData(String name,String Age,String Hobby,String Email);

    /**=======================================================================================*/
    /**撈取全部資料*/
    //**
    // 監聽資料表，三種observable。
    // /
    @Query("SELECT * FROM "+ tableName + " WHERE id = :name")
    public abstract Flowable<List<MyData>> getMyData(String name);

    @Query("SELECT * FROM " + tableName)
    Flowable<List<MyData>> getFlowable();

    @Query("SELECT * FROM " + tableName)
    Maybe<List<MyData>> getMaybe();

    @Query("SELECT * FROM " + tableName)
    Single<List<MyData>> getSingle();
    /******************************************************************************************/
    @Query("SELECT * FROM " + tableName)
    List<MyData> displayAll();

    /**撈取某個名字的相關資料*/
    @Query("SELECT * FROM " + tableName +" WHERE id = :id")
    List<MyData> findDataByName(String id);

    /**=======================================================================================*/
    /**簡易更新資料的方法*/
    @Update
    void updateData(MyData myData);

    /**複雜(?)更新資料的方法*/
    @Query("UPDATE "+tableName+" SET name = :name,Age=:Age,Hobby=:Hobby,Email = :Email WHERE id = :id" )
    void updateData(int id,String name,String Age,String Hobby,String Email);

    /**=======================================================================================*/
    /**簡單刪除資料的方法*/
    @Delete
    void deleteData(MyData myData);

    /**複雜(?)刪除資料的方法*/
    @Query("DELETE  FROM " + tableName + " WHERE id = :id")
    void deleteData(int id);

}

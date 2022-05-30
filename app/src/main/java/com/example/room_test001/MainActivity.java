package com.example.room_test001;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.room_test001.database.DataBase;
import com.example.room_test001.database.DataUao;
import com.example.room_test001.database.MyData;

//Stetho工具，需要另外加入implementation in gradle.
import com.facebook.stetho.Stetho;

import org.reactivestreams.Subscription;

import java.util.List;

import io.reactivex.FlowableSubscriber;
import io.reactivex.MaybeObserver;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    MyAdapter myAdapter;
    MyData nowSelectedData;
    RecyclerView recyclerView;
    EditText editName;
    EditText editAge;
    EditText editHobby;
    EditText editEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this);    //Facebook開發的調適工具Stetho，設置資料庫監視。

        myAdapter = new MyAdapter(this);


        Button btCreate = findViewById(R.id.create_text);
        Button btClear = findViewById(R.id.clear_text);
        Button btModify = findViewById(R.id.modify_text);
        Button btRefresh = findViewById(R.id.refrash_text);
        editName = findViewById(R.id.editname);
        editAge = findViewById(R.id.editage);
        editHobby = findViewById(R.id.edithobby);
        editEmail = findViewById(R.id.editemail);
        recyclerView = findViewById(R.id.recyclerviewinfo);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//設置分隔線

/**建立被觀察者**/
/**Consumer是Rxjava的一個簡寫方式**/
//**
//      DataBase.getInstance(this).getDataUao().getALL()
//                        .subscribeOn(Schedulers.io())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                        .subscribe(new Consumer<List<MyData>>() {
//                                            @Override
//                                            public void accept(List<MyData> myData) throws Exception {
//                                                List<MyData> mUserList = myData;
//
//                                            }
//                                        }, new Consumer<Throwable>() {
//                                            @Override
//                                            public void accept(Throwable throwable) throws Exception {
//                                                Log.e("TTT","OnError"+throwable);
//                                            }
//                                        }, new Action() {
//                                            @Override
//                                            public void run() throws Exception {
//                                                Log.e("TTT","Complete");
//                                            }
//                                        }, new Consumer<Subscription>() {
//                                            @Override
//                                            public void accept(Subscription subscription) throws Exception {
//                                                Log.e("TTT","Subscription"+subscription);
//                                            }
//
//                                        });
// /

/**Flowable觀察者**/
        DataBase.getInstance(this).getDataUao().getFlowable()
                        .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new FlowableSubscriber<List<MyData>>() {
                                            @Override
                                            public void onSubscribe(Subscription s) {
                                                Log.e("Flowable","OnSubscribe"+s);
                                            }

                                            @Override
                                            public void onNext(List<MyData> myData) {
                                                Log.e("Flowable","onNEXT");
                                            }

                                            @Override
                                            public void onError(Throwable t) {
                                                Log.e("Flowable","OnError"+t);
                                            }

                                            @Override
                                            public void onComplete() {
                                                Log.e("Flowable","Complete");
                                            }
                                        });

/**Maybe觀察者**/
//如果沒有撈到資料時會進入onComplete，有撈取到資料會進入onSuccess。屬於一次性查詢。
        DataBase.getInstance(this).getDataUao().getMaybe()
                        .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new MaybeObserver<List<MyData>>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {
                                                Log.e("Maybe","Subscription "+d);
                                            }

                                            @Override
                                            public void onSuccess(List<MyData> myData) {
                                                Log.e("Maybe","onSuccess "+myData);
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Log.e("Maybe","OnError "+e);
                                            }

                                            @Override
                                            public void onComplete() {
                                                Log.e("Maybe","Complete");
                                            }
                                        });
/**Single觀察者**/
        DataBase.getInstance(this).getDataUao().getSingle()
                        .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new SingleObserver<List<MyData>>() {
                                            @Override
                                            public void onSubscribe(Disposable d) {
                                                Log.e("Single","Subscription "+d);
                                            }

                                            @Override
                                            public void onSuccess(List<MyData> myData) {
                                                Log.e("Single","onSuccess "+myData);
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                Log.e("Single","OnError "+e);
                                            }
                                        });
        setRecyclerFunction(recyclerView);//設置RecyclerView左滑刪除
        //設定更改資料的事件
        btModify.setOnClickListener((view) ->{
            new Thread(() -> {
                if (nowSelectedData ==null) return;
                String name = editName.getText().toString();
                String Age = editAge.getText().toString();
                String Hobby = editHobby.getText().toString();
                String Email = editEmail.getText().toString();
                MyData data = new MyData(nowSelectedData.getId(),name,Age,Hobby,Email);
                DataBase.getInstance(this).getDataUao().updateData(data);
                runOnUiThread(()->{
                    editName.setText("");
                    editAge.setText("");
                    editEmail.setText("");
                    editHobby.setText("");
                    nowSelectedData = null;
                    myAdapter.refreshView();

                    Toast.makeText(this,"已更新資訊!",Toast.LENGTH_LONG).show();
                });
            }).start();
        });

        //清空資料事件按鈕
        btClear.setOnClickListener((view -> {
            editName.setText("");
            editAge.setText("");
            editHobby.setText("");
            editEmail.setText("");
            nowSelectedData = null;
            Toast.makeText(this,"已清空",Toast.LENGTH_LONG).show();
        }));

        //========================================//
        /**新增資料**/
        //問題:無法更新RecyclerView **解決**
        btCreate.setOnClickListener((view -> {
            new Thread(() -> {
               String name = editName.getText().toString();
               String Age = editAge.getText().toString();
               String Hobby = editHobby.getText().toString();
               String Email = editEmail.getText().toString();
               if (name.length() == 0) return;
               MyData data = new MyData(name,Age,Hobby,Email);
               DataBase.getInstance(this).getDataUao().insertData(data);
               refrashed();
               runOnUiThread(()->{
                   myAdapter.refreshView();
                   editName.setText("");
                   editAge.setText("");
                   editHobby.setText("");
                   editEmail.setText("");
                   Toast.makeText(this,"以新增資料",Toast.LENGTH_LONG).show();
               });
//
//                myAdapter.refreshView();
            }).start();
        }));
//**
// "刷新資料表"的按鈕事件。
// 因為在剛開啟時沒有顯示資料表所存在的資料，若要顯示資料表必須先"新增資料"進去。
// 所以我新增一個refresh按鈕，在一開始時先抓到資料表並顯示在recyclerview。
// /
        btRefresh.setOnClickListener(View -> {
            new Thread(()->{
                DataBase.getInstance(this).getDataUao().displayAll();
                refrashed();
                runOnUiThread(()->{
                    myAdapter.refreshView();
                    Toast.makeText(this,"已刷新資料表",Toast.LENGTH_LONG).show();
                });
            }).start();
            //getMyData
        });
    }
    //初始化RecyclerView的表格
    public void refrashed(){
//        new Thread(()->{          //這邊已經在一個Thread中了，因此若在主程式中再宣告進另一個Thread中會有問題。
            List<MyData> data = DataBase.getInstance(this).getDataUao().displayAll();

            runOnUiThread(()->{
                recyclerView.setAdapter(myAdapter);

                myAdapter.setData(data);
                myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(MyData myData) {
                    }
                });
            //**
            // 取得在RecyclerView被選取的資料表欄位並顯示在上方的欄位中。
            // /
                myAdapter.setOnItemClickListener((myData) -> {
                    nowSelectedData = myData;
                    editName.setText(myData.getName());
                    editAge.setText(myData.getAge());
                    editHobby.setText(myData.getHobby());
                    editEmail.setText(myData.getEmail());
                });
            });
//        });
    }
    /**設置RecyclerView的左滑刪除行為*/
    public void setRecyclerFunction(RecyclerView recyclerView){
        ItemTouchHelper helper = new ItemTouchHelper(new ItemTouchHelper.Callback() {//設置RecyclerView手勢功能
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                //**
                // Returns the Adapter position of the item represented by this ViewHolder with respect to the RecyclerView's RecyclerView.Adapter.
                //
                // If the RecyclerView.Adapter that bound this RecyclerView.ViewHolder is inside another adapter (e.g. ConcatAdapter),
                // this position might be different and will include the offsets caused by other adapters in the ConcatAdapter.
                //
                // /
                int position = viewHolder.getAdapterPosition(); //可能以後會被淘汰??

                switch (direction){
                    case ItemTouchHelper.LEFT:
                    case ItemTouchHelper.RIGHT:
                        myAdapter.deleteData(position);
                        break;
                }
            }
        });
        helper.attachToRecyclerView(recyclerView);
    }

}
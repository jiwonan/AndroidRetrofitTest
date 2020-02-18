package kr.hs.emirim.jiwonan.retrofittest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WriteActivity extends AppCompatActivity {

    private final String fileName = "items.list";

    private ListView listview;
    private ArrayAdapter adapter;
    private ArrayList<String> items = new ArrayList<String>();
    private Bitmap selPhoto;

    ImageView imageView;
    String username;
    String content;

    SharedPreferences LoginUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        imageView = findViewById(R.id.galleryview);
        EditText contentEdit = findViewById(R.id.content_user);
        content = contentEdit.getText().toString();

        // Retrofit.Builder();
        Retrofit retrofit = (new Retrofit.Builder()).baseUrl("http://192.168.9.162:1234").addConverterFactory(GsonConverterFactory.create()).build();
        final RedayService apiService = retrofit.create(RedayService.class);

        listview = (ListView) findViewById(R.id.listview1);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, items);

        listview.setAdapter(adapter);

        loadItemsFromFile();
        adapter.notifyDataSetChanged();

        LoginUserInfo = getSharedPreferences("userlogininfo", MODE_PRIVATE);
        // text에 key값이 저장되어있는지 확인. 아무 값도 없으면 ""반환. 값 가져오기.
        final String ee = LoginUserInfo.getString("email", null);

        // username 받아오기.
        Call<String> apiCall = apiService.getUsername(ee);
        apiCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                username = response.body();
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("mytag", t.toString());
            }
        });


        Button buttonAdd = (Button) findViewById(R.id.buttonAdd) ;
        buttonAdd.setEnabled(false) ; // 초기 버튼 상태 비활성 상태로 지정.
        buttonAdd.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                selPhoto.compress(Bitmap.CompressFormat.PNG, 100, stream);

                RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), stream.toByteArray());
                MultipartBody.Part part = MultipartBody.Part.createFormData("file", "filename.png", requestFile);
                content="!1";
                username = "asd123";
                Call<String> apicall = apiService.createArticle(username, content, part);
                Log.d("mytag", "call file api");
                apicall.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        String s = response.body();
                        Log.d("mytag",s);
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("mytag", t.toString());
                    }
                });
            }
        });

        Button buttonDel = (Button) findViewById(R.id.buttonDel) ;
        buttonDel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count ;
                int checkedIndex ;

                count = adapter.getCount() ;

                if (count > 0) {
                    // 리스트뷰에서 선택된 아이템 인덱스 얻어오기.
                    checkedIndex = listview.getCheckedItemPosition();
                    if (checkedIndex > -1 && checkedIndex < count) {
                        // 아이템 삭제
                        items.remove(checkedIndex) ;

                        // 리스트뷰 선택 초기화.
                        listview.clearChoices();

                        // 리스트뷰 갱신
                        adapter.notifyDataSetChanged();

                        // 리스트뷰 아이템들을 파일에 저장.
                        saveItemsToFile() ;
                    }
                }
            }
        });

        EditText editTextNew = (EditText) findViewById(R.id.editTextNew) ;
        editTextNew.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable edit) {
                Button buttonAdd = (Button) findViewById(R.id.buttonAdd) ;
                if (edit.toString().length() > 0) {
                    // 버튼 상태 활성화.
                    buttonAdd.setEnabled(true) ;
                } else {
                    // 버튼 상태 비활성화.
                    buttonAdd.setEnabled(false) ;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        Button getPictureBtn = findViewById(R.id.get_picture);
        getPictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callGallery();
            }
        });
    }

    final int REQ_SELECT = 1;

    // 갤러리 이미지 얻어오기.
    public void callGallery() {
        Uri uri = Uri.parse("content://media/external/images/media");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,REQ_SELECT);
    }

    /*
    // 카메라로 찍기.
    public void tkePricure(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }
    */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            if(requestCode == REQ_SELECT) {
                Log.d("mytag", intent.getData().toString());
                // 인텐트에 데이터가 담겨있다면
                if(!intent.getData().equals(null)) {
                    // intent에 담긴 이미지를 uri를 이용해서 bitmap 형태로 읽어온다.
                    selPhoto = MediaStore.Images.Media.getBitmap(getContentResolver(), intent.getData());
                    selPhoto = Bitmap.createScaledBitmap(selPhoto, 500, 500, true);
                    imageView.setImageBitmap(selPhoto);
                    Log.d("mytag", "selPhoto : " + selPhoto);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        /*
        // 선택한 이미지의 uri를 읽어옴.
        Uri selPhotoUri = intent.getData();
        Log.e("전송", "시작");
        String urlString = "http://192.168.1.69:1234"; //업로드 할 서버의 url 주소
        // 절대 경로를 획득.
        Cursor c = getContentResolver().query(Uri.parse(selPhotoUri.toString()), null, null, null, null);
        c.moveToNext();
        // 업로드할 파일의 절대경로 얻어옴. ("_data") 도 ㄱㅊ.
        String absolutePath = c.getString(c.getColumnIndex(MediaStore.MediaColumns.DATA));
        Log.e("*** 파일의 절대 경로 ***", absolutePath);
        // 파일 업로드 시작.
        HttpFileUpload(urlString, "",absolutePath);

         */
    }

    private void saveItemsToFile() {
            File file = new File(getFilesDir(), fileName);
            FileWriter fw = null;
            BufferedWriter bufwr = null;

            try {
                // open file.
                fw = new FileWriter(file);
                bufwr = new BufferedWriter(fw);

                for (String str : items) {
                    bufwr.write(str);
                    bufwr.newLine();
                }

                // write data to the file.
                bufwr.flush();

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                // close file.
                if (bufwr != null) {
                    bufwr.close();
                }

                if (fw != null) {
                    fw.close();
                }
            } catch (Exception e) {
                e.printStackTrace() ;
            }
        }

        private void loadItemsFromFile() {
            File file = new File(getFilesDir(), fileName);
            FileReader fr = null;
            BufferedReader bufrd = null;
            String str;

            if (file.exists()) {
                try {
                    // open file.
                    fr = new FileReader(file);
                    bufrd = new BufferedReader(fr);

                    while ((str = bufrd.readLine()) != null) {
                        items.add(str);
                    }

                    bufrd.close();
                    fr.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
}

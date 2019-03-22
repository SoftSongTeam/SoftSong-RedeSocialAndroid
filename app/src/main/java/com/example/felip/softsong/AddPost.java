package com.example.felip.softsong;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.felip.softsong.Cadastro_Screen.image;


public class AddPost extends Activity implements PopupMenu.OnMenuItemClickListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_post);
        Button postar = (Button) findViewById(R.id.btn_postar);
        ImageView add = (ImageView) findViewById(R.id.addarq);
        desc = (TextView) findViewById(R.id.desc);
        postar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new post(images).execute();
            }
        });
        viewPager = (ViewPager) findViewById(R.id.files);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowPopup(view);
            }
        });
    }

    public void ShowPopup(View v)
    {
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_post);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        File PictureDirectory;
        Uri data;
        Intent Photopicker;
        switch (menuItem.getItemId())
        {
            case R.id.pic:
                Photopicker = new Intent(Intent.ACTION_PICK);
                PictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                pictureDirectoryPath = PictureDirectory.getPath();
                sftpCaminho = pictureDirectoryPath;
                data = Uri.parse(pictureDirectoryPath);
                Photopicker.setDataAndType(data, "image/*");
                startActivityForResult(Photopicker, IMAGE_GALLEY_REQUEST);
                return true;
            case R.id.video:
                Photopicker = new Intent(Intent.ACTION_PICK);
                PictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                pictureDirectoryPath = PictureDirectory.getPath();
                sftpCaminho = pictureDirectoryPath;
                data = Uri.parse(pictureDirectoryPath);
                Photopicker.setDataAndType(data, "video/*");
                startActivityForResult(Photopicker, VIDEO_GALLEY_REQUEST);
                return true;
            case R.id.audio:
                Photopicker = new Intent(Intent.ACTION_PICK);
                PictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                pictureDirectoryPath = PictureDirectory.getPath();
                sftpCaminho = pictureDirectoryPath;
                data = Uri.parse(pictureDirectoryPath);
                Photopicker.setDataAndType(data, "audio/*");
                startActivityForResult(Photopicker, AUDIO_GALLEY_REQUEST);
                return true;
            case R.id.doc:
                Photopicker = new Intent(Intent.ACTION_PICK);
                PictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
                pictureDirectoryPath = PictureDirectory.getPath();
                sftpCaminho = pictureDirectoryPath;
                data = Uri.parse(pictureDirectoryPath);
                Photopicker.setDataAndType(data, "*/*");
                startActivityForResult(Photopicker, ARCHIVE_GALLEY_REQUEST);
                return true;
                default:
                    return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ActivityCompat.shouldShowRequestPermissionRationale(AddPost.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (resultCode == RESULT_OK) {

                //Uri imageUri = data.getData();
                sftpCaminho = data.getData().getPath().substring(5);
                file = new File(sftpCaminho);
                try {

                    //Toast.makeText(Cadastro.this, data.getData().getPath(), Toast.LENGTH_SHORT).show();
                    inputstream = getContentResolver().openInputStream(data.getData());
                    image = BitmapFactory.decodeStream(inputstream);
                    images.add(sftpCaminho);
                    ViewPageadapt viewPageadapt = new ViewPageadapt(this, images);
                    viewPager.setAdapter(viewPageadapt);
                } catch (FileNotFoundException e) {
                    Toast.makeText(this, "Nao foi possivel abrir a foto selecionada.", Toast.LENGTH_LONG).show();
                } catch (IOException e) {

                }



        }
    }

    class ViewPageadapt extends PagerAdapter {

        Activity activity;
        ArrayList<String> imagess;
        LayoutInflater inflater;
        public ViewPageadapt(Activity activity, ArrayList<String> imagens)
        {
            this.activity = activity;
            this.imagess = imagens;
        }
        @Override
        public int getCount() {
            try{return imagess.size();}
            catch(Exception e) {return 1;}
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view==object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
            inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View[] itemViews = new View[1];
            final String[] a = new String[1];
            Thread threadd = new Thread() {
                @Override
                public void run() {
                    try {
                        a[0] = imagess.get(position);
                        System.out.println(a[0]);
                        if(a[0].contains("jpg") || a[0].contains("png")) {
                            final View itemView = inflater.inflate(R.layout.postpics, container, false);
                            final ImageView images = (ImageView) itemView.findViewById(R.id.imgpic);
                            DisplayMetrics dis = new DisplayMetrics();
                            activity.getWindowManager().getDefaultDisplay().getMetrics(dis);
                            images.setMinimumHeight(dis.heightPixels);
                            images.setMinimumWidth(dis.widthPixels);
                            Thread thread = new Thread() {
                                @Override
                                public void run() {
                                    try {
                                        Glide.with(activity.getApplicationContext()).load( (imagess.get(position))).into(images);
                                    } catch (Exception e) {
                                        //Picasso.with(activity.getApplicationContext()).load(R.drawable.ops).placeholder(R.drawable.ops).into(images);
                                    }
                                }
                            };
                            thread.run();
                            container.addView(itemView);
                            itemViews[0] = itemView;
                        }
                        else if(a[0].contains("mp4") || a[0].contains("avi"))
                        {
                            inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View itemView = inflater.inflate(R.layout.postvideo, container, false);
                            final VideoView video = (VideoView) itemView.findViewById(R.id.imgvid);
                            try {
                                if(!video.isPlaying()) {
                                    Uri uri = Uri.parse(imagess.get(position));
                                    video.setVideoURI(uri);
                                }

                            }
                            catch (Exception e){}
                            video.requestFocus();
                            video.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(video.isPlaying())
                                        video.pause();
                                    else
                                        video.start();
                                }
                            });
                            container.addView(itemView);
                            itemViews[0] = itemView;
                        }
                        else if(a[0].contains("mp3") || a[0].contains("wav") || a[0].contains("m4a"))
                    {
                        inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        final View itemView = inflater.inflate(R.layout.postaudio, container, false);
                        TextView audio = (TextView) itemView.findViewById(R.id.nomeAudio);
                        audio.setText(imagess.get(position).substring(imagess.get(position).lastIndexOf("/") + 1));
                        container.addView(itemView);
                        itemViews[0] = itemView;
                    }
                    else
                        {
                            inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            final View itemView = inflater.inflate(R.layout.postarquivos, container, false);
                            final TextView name = (TextView) itemView.findViewById(R.id.nomeArquivo);
                            final ImageView pic = (ImageView) itemView.findViewById(R.id.doc);
                            final String nome = (imagess.get(position).substring(imagess.get(position).lastIndexOf("/")+1));
                            Thread t = new Thread()
                            {
                                @Override
                                public void run() {
                                    name.setText(nome);
                                    pic.setImageResource(R.drawable.ico_doc);
                                }
                            };
                            t.run();
                            //Toast.makeText(getApplicationContext(), "A", Toast.LENGTH_LONG).show();
                            container.addView(itemView);
                            itemViews[0] = itemView;
                        }
                }
                    catch (Exception e){}
                }
            };
            threadd.run();
            return itemViews[0];
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }
    }

    public class post extends AsyncTask<String,String,String>
    {
        ClasseConexao conexao = new ClasseConexao();
        ArrayList<String> imagess;
        public post(ArrayList<String> images) {
            imagess = images;
        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... strings) {
            try {
                Connection connection = conexao.CONN();
                if(connection != null)
                {
                    final Statement stmt = connection.createStatement();
                    for(int i = 0; i < imagess.size(); i++)
                    {
                        final ResultSet rs = stmt.executeQuery("Select Count(*) from tblPost");
                        final String[] h = {""};
                        final String g = h[0];
                        final String[] base64File = {""};
                        String name = "";
                        if(!imagess.get(i).contains("/storage/emulated/0/"))
                            name = imagess.get(i).replace("rnal_files/", "/storage/emulated/0/");
                        else {
                            name = imagess.get(i);
                        }
                        final File file = new File(name);
                        try (FileInputStream imageInFile = new FileInputStream(file)) {
                            final String finalName = name;
                            final int finalI = i;
                            Thread t = new Thread(){
                                @Override
                                public void run() {
                                    try {
                                        if(rs != null && rs.next())
                                            h[0] = rs.getString("Count(*)");
                                        byte fileData[] = new byte[(int) file.length()];
                                        imageInFile.read(fileData);
                                        base64File[0] = Base64.encodeToString(fileData, Base64.DEFAULT);
                                        String path = ((h[0] + 1) + "-" + finalName.substring(finalName.lastIndexOf("/"), finalName.length())).replace("/","");
                                        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                        Date date = new Date();
                                        stmt.executeQuery("{CALL Adiciona_posts(" + Login_Screen.sharedPref.getString("id","") + ",'" + desc.getText() + "','" + dateFormat.format(date) + "','" + path + "'," + finalI +")}");
                                        System.out.println(finalI);
                                        new HTTPServer(path, base64File[0]).execute();
                                    }
                                    catch (Exception e){}
                                }
                            };
                            t.run();
                            // Reading a file from file system


                        } catch (FileNotFoundException e) {
                            System.out.println("File not found" + e);
                        } catch (IOException ioe) {
                            System.out.println("Exception while reading the file " + ioe);
                        }
                    }
                    //stmt.executeUpdate("");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(AddPost.this, "Post adicionado com sucesso", Toast.LENGTH_LONG).show();
        }
    }

    ArrayList<String> images = new ArrayList<>();
    ViewPager viewPager;
    ArquivosDiversos arquiv = new ArquivosDiversos();
    static final int IMAGE_GALLEY_REQUEST = 20;
    static final int VIDEO_GALLEY_REQUEST = 25;
    static final int AUDIO_GALLEY_REQUEST = 30;
    static final int ARCHIVE_GALLEY_REQUEST = 35;
    String pictureDirectoryPath;
    public static String sftpCaminho;
    public static File file;
    public ImageView img;
    public InputStream inputstream;
    static TextView desc;
}
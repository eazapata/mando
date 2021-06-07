package com.example.killercontroller.Interface;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;

import com.example.killercontroller.Communication.Message;
import com.example.killercontroller.Data.Singleton;
import com.example.killercontroller.R;

public class ConfigureActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageSwitcher img_switcher;
    private TextView textViewPlayerName, readyButton;
    private String playerName;
    private ImageView back, next;
    private int imgList[] = {R.drawable.nave1,
            R.drawable.nave_tipo2,
            R.drawable.nave_tipo3};
    private int currentIndex = 0;
    private int count = imgList.length;
    private Singleton instance;
    private int idServer;
    private Button blueTeam, redTeam;
    private final String NICKNAME = "NICKNAME", TEAM = "TEAM", READY = "READY", SPACECRAFT_TYPE = "SPACECRAFT TYPE", ADMIN = "ADMIN";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        this.instance = Singleton.getInstance();
        this.img_switcher = (ImageSwitcher) findViewById(R.id.img_switcher);

        this.textViewPlayerName = (TextView) findViewById(R.id.player_name);
        setPlayerName();

        this.img_switcher = (ImageSwitcher) findViewById(R.id.img_switcher);
        img_switcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imgView = new ImageView(getApplicationContext());
                imgView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imgView.setLayoutParams(new ImageSwitcher.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.FILL_PARENT
                ));
                return imgView;
            }
        });
        img_switcher.setImageResource(imgList[0]);

        this.back = (ImageView) findViewById(R.id.back_arrow);
        this.back.setOnClickListener(this);
        this.next = (ImageView) findViewById(R.id.next_arrow);
        this.next.setOnClickListener(this);
        this.blueTeam = (Button) findViewById(R.id.blue_team);
        this.blueTeam.setOnClickListener(this);
        this.redTeam = (Button) findViewById(R.id.red_team);
        this.redTeam.setOnClickListener(this);
        this.readyButton = (TextView) findViewById(R.id.ready_button);
        this.readyButton.setOnClickListener(this);

    }

    public void exitGame() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pause_menu);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setBackgroundDrawableResource(R.color.translucent_black);

        Button continues = dialog.findViewById(R.id.continue_btn);
        continues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button exit = dialog.findViewById(R.id.exit_btn);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigureActivity.this.finish();
            }
        });
        dialog.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back_arrow:
                this.img_switcher.setInAnimation(this, R.anim.from_right);
                this.img_switcher.setOutAnimation(this, R.anim.to_left);
                --this.currentIndex;
                if (currentIndex < 0) {
                    this.currentIndex = this.imgList.length - 1;
                }
                this.img_switcher.setImageResource(this.imgList[this.currentIndex]);
                break;
            case R.id.next_arrow:
                this.img_switcher.setInAnimation(this, R.anim.from_left);
                this.img_switcher.setOutAnimation(this, R.anim.to_right);
                ++this.currentIndex;
                if (currentIndex == count) {
                    this.currentIndex = 0;
                }
                this.img_switcher.setImageResource(this.imgList[this.currentIndex]);
                break;
            case R.id.blue_team:
                //PASAR IP DE LA ACTIVIDAD ANTERIOR
                Message message = new Message();
                message.setMessageType("TEAM");
                message.setMessage("blue");
                this.instance.getNodeManager().send(idServer, message);
                break;
            case R.id.red_team:
                Message redMessage = new Message();
                redMessage.setMessageType("TEAM");
                redMessage.setMessage("red");
                this.instance.getNodeManager().send(idServer, redMessage);
                break;
            case R.id.ready_button:
                Message shipType = new Message();
                shipType.setMessageType(String.valueOf(this.currentIndex));
                shipType.setMessage(SPACECRAFT_TYPE);
                Message readyMessage = new Message();
                readyMessage.setMessageType("READY");
                readyMessage.setMessage("true");
                this.instance.getNodeManager().send(idServer, shipType);
                this.instance.getNodeManager().send(idServer, readyMessage);
                startPadActivity(v);
                break;
            default:
                System.out.println("Invalid option");
        }
    }

    public void startPadActivity(View v) {

        System.out.println(textViewPlayerName.getText().toString());
        Intent intent;
        intent = new Intent(this, PadActivity.class);
        intent.putExtra("SHIP", this.imgList[this.currentIndex]);
        startActivity(intent);
        ConfigureActivity.this.finish();

    }

    private void setPlayerName() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        playerName = extras.getString("PLAYER KEY");
        idServer = extras.getInt("ID NODE SERVER");
        System.out.println(idServer);
        System.out.println(idServer);
        System.out.println(idServer);
        System.out.println(idServer);

        textViewPlayerName.setText(playerName);
    }

    @Override
    public void onBackPressed() {
        exitGame();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.instance.getMediaPlayer() != null && !this.instance.getMediaPlayer().isPlaying() ) this.instance.getMediaPlayer().start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.instance.getMediaPlayer() != null) {
            this.instance.getMediaPlayer().pause();
        }
    }
}

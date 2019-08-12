package com.jinpo.screenautomation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jinpo.screenautomationlib.ICancelAuto;
import com.jinpo.screenautomationlib.IChangeAuto;
import com.jinpo.screenautomationlib.ScreenAutomation;

public class MainActivity extends AppCompatActivity implements IChangeAuto{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScreenAutomation.setScreenAuto(this);
        setContentView(R.layout.activity_main);
    }

    @Override
    public float newScreenWidth() {
        return 300;
    }
}

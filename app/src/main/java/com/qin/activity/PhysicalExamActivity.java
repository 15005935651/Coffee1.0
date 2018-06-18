package com.qin.activity;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.coffee.blelibrary.BleController;
import com.coffee.blelibrary.callback.ConnectCallback;
import com.coffee.blelibrary.callback.ScanCallback;
import com.mingle.widget.LoadingView;
import com.qin.R;
import com.qin.adapter.ble.DeviceListAdapter;
import com.qin.util.ScreenUtils;
import com.qin.util.ToastUtils;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class PhysicalExamActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.physical_toolbar)
    Toolbar physicalToolbar;
    @BindView(R.id.mDeviceList)
    ListView mDeviceList;
    @BindView(R.id.phy_exam_refreshLayout)
    SmartRefreshLayout phyExamRefreshLayout;
    @BindView(R.id.loadView)
    LoadingView loadView;

    private BleController mBleController;
    private List<BluetoothDevice> bluetoothDevices = new ArrayList<BluetoothDevice>();


    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical_exam);
        ButterKnife.bind(this);

        physicalToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhysicalExamActivity.this.finish();
            }
        });

        mDeviceList = (ListView) findViewById(R.id.mDeviceList);

        // TODO  第一步：初始化
        mBleController = BleController.getInstance().init(this);
        loadView.setVisibility(View.GONE);
        initDialog();
        mDialog.show();

        // TODO  第二步：搜索设备，获取列表后进行展示
        scanDevices();

        phyExamRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                scanDevices();
            }
        });
        phyExamRefreshLayout.setPrimaryColorsId(R.color.colorGreen, android.R.color.white);
    }


    /*搜寻蓝牙设备**/
    private void scanDevices() {
        mBleController.scanBle(0, new ScanCallback() {
            @Override
            public void onSuccess() {
                mDialog.dismiss();
                if (bluetoothDevices.size() > 0) {
                    mDeviceList.setAdapter(new DeviceListAdapter(getApplication(), bluetoothDevices));
                    mDialog.dismiss();
                    phyExamRefreshLayout.finishRefresh();
                    mDeviceList.setOnItemClickListener(PhysicalExamActivity.this);
                } else {
                    ToastUtils.showBgResource(getApplication(), "未搜索到Ble设备");
                }
            }

            @Override
            public void onScanning(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (!bluetoothDevices.contains(device)) {
                    bluetoothDevices.add(device);
                }
            }
        });
    }


    private void initDialog() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ScreenUtils.getWindowWidth(PhysicalExamActivity.this), ScreenUtils.getWindowHeight(PhysicalExamActivity.this));
        params.width = (int) (PhysicalExamActivity.this.getWindowManager().getDefaultDisplay().getWidth() * 0.5f);
        params.height = (int) (PhysicalExamActivity.this.getWindowManager().getDefaultDisplay().getWidth() * 0.5f);
        mDialog = new Dialog(PhysicalExamActivity.this);
        View view = View.inflate(PhysicalExamActivity.this, R.layout.dialog_loading, null);
        mDialog.setContentView(view);
        mDialog.setContentView(view, params);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.setCancelable(true);

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, final long l) {
        mDialog.show();
        // TODO 第三步：点击条目后,获取地址，根据地址连接设备
        String address = bluetoothDevices.get(i).getAddress();
        mBleController.connect(0, address, new ConnectCallback() {
            @Override
            public void onConnSuccess() {
                mDialog.dismiss();
                mDeviceList.setVisibility(View.GONE);
                loadView.setLoadingText("连接成功，正在为您体检...");
                loadView.setVisibility(View.VISIBLE);
//                String [] s={"hello","go start","turn right","turn left","care for","speed 3m/s"};
//                try{
//                    for(int i=0;i<s.length;i++){
//                        Thread.currentThread().sleep(2000);
//                        mBleController.writeBuffer(s[i].getBytes(), new OnWriteCallback() {
//                            @Override
//                            public void onSuccess() {
//
//                            }
//
//                            @Override
//                            public void onFailed(int state) {
//
//                            }
//                        });
//                    }
//                }catch (InterruptedException e){
//                    e.printStackTrace();
//                }
                //  Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                //  startActivity(new Intent(MainActivity.this,SendAndReciveActivity.class));
            }

            @Override
            public void onConnFailed() {
                mDialog.dismiss();
                ToastUtils.showBgResource(getBaseContext(), "连接超时请重试");
            }
        });
    }

}

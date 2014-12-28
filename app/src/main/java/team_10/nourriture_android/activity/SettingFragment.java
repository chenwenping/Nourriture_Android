package team_10.nourriture_android.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import team_10.nourriture_android.R;
import team_10.nourriture_android.application.MyApplication;


/**
 * Created by ping on 2014/12/20.
 */
public class SettingFragment extends Fragment {

    private Button exit_btn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        exit_btn = (Button)getActivity().findViewById(R.id.btn_exit);
        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(getActivity());
                dialog.setTitle("Confirm exit").setIcon(android.R.drawable.ic_dialog_info).setMessage("Do you really want to exit").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // clear login information
                        MyApplication.getInstance().clearUserBean();
                        MyApplication.getInstance().islogin = false;
                        Toast.makeText(getActivity(), "exit successfully", Toast.LENGTH_SHORT).show();
                        exit_btn.setVisibility(View.GONE);
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
            }
        });
    }
}

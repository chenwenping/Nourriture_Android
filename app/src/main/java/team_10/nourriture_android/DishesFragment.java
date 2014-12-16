package team_10.nourriture_android;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DishesFragment extends Fragment {
    ArrayList<String> dishesNameList;
    ListView dishesList;
    ProgressDialog progress;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dishes, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dishesList = (ListView) getActivity().findViewById(R.id.listViewDishes);
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading...");
        progress.show();
        try {
            getAllDishes();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getAllDishes() throws JSONException {
        NourritureRestClient.get("dishes", null, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                progress.dismiss();
                try {
                    dishesNameList = new ArrayList<>();
                    JSONObject item;
                    for (int i = 0; i != response.length(); ++i) {
                        item = response.getJSONObject(i);
                        dishesNameList.add(item.getString("name"));
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dishesNameList);
                    dishesList.setAdapter(arrayAdapter);
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
package com.accenture.openweatheracntest.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.accenture.openweatheracntest.R;
import com.accenture.openweatheracntest.common.Globals;
import com.accenture.openweatheracntest.common.api.OWService;
import com.accenture.openweatheracntest.model.WeatherGroup;
import com.accenture.openweatheracntest.model.WeatherResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.rilixtech.materialfancybutton.MaterialFancyButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link MainListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainListFragment extends Fragment {

    private OWService service;
    protected WeatherGroup weatherGroup;
    protected Thread thread;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private MaterialFancyButton refreshButton;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public MainListFragment() {
        // Required empty public constructor
    }

    public static MainListFragment newInstance(String param1, String param2) {
        MainListFragment fragment = new MainListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Globals.apiEndpoint)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(OWService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_main_list, container, false);

        mRecyclerView = layout.findViewById(R.id.fragment_main_list_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        refreshButton = layout.findViewById(R.id.fragment_main_list_refresh);

        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();
        refreshButton.setOnClickListener((View v) -> {
            retrieveWeatherData();
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (weatherGroup == null) {
            retrieveWeatherData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        thread.interrupt();
    }

    private void retrieveWeatherData() {
        ArrayList<String> listOfCityId = new ArrayList<String>();
        listOfCityId.add(Globals.cityIdLondon);
        listOfCityId.add(Globals.cityIdPrague);
        listOfCityId.add(Globals.cityIdSanFran);
        String cityIdParams = generateIdString(listOfCityId);

        Call<WeatherGroup> weatherList = service.weatherList(cityIdParams, Globals.appId);
        Runnable getAllWeather = () -> {
            try {
                WeatherGroup response = weatherList.execute().body();
                if (response != null) {
                    weatherGroup = response;
                    getActivity().runOnUiThread(() ->{
                        showItems(response.getList());
                    });
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        };
        thread = new Thread(getAllWeather);
        thread.start();
    }

    private void showItems(List<WeatherResponse> weatherResponses) {
        mAdapter = new WeatherAdapter(weatherResponses);
        if (mRecyclerView.getAdapter() == null) {
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    private String generateIdString(List<String> cityIdList) {
        String value = "";

        for (int i = 0; i < cityIdList.size(); i++) {
            if ( i != cityIdList.size()-1) {
                value += cityIdList.get(i) + ",";
            } else {
                value += cityIdList.get(i);
            }
        }
        return value;
    }

    private void openWeatherDetail(WeatherResponse weatherResponse) {
        Intent intent = new Intent();
        intent.putExtra("weather", weatherResponse);
        intent.setClassName(getActivity().getApplicationContext(),
                "com.accenture.openweatheracntest.view.activity.WeatherDetailActivity");
        startActivity(intent);
    }

    class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder>{

        private List<WeatherResponse> weatherResponses;

        class ViewHolder extends RecyclerView.ViewHolder {
            public TextView labelName;
            public TextView labelDesc;
            public TextView labelTemp;
            public LinearLayout container;

            public ViewHolder(View view) {
                super(view);
                labelName = view.findViewById(R.id.item_weather_main_name);
                labelDesc = view.findViewById(R.id.item_weather_main_actual_weather);
                labelTemp = view.findViewById(R.id.item_weather_main_temp);
                container = view.findViewById(R.id.item_weather_main_container);
            }
        }

        public WeatherAdapter(List<WeatherResponse> weatherResponses) {
            this.weatherResponses = weatherResponses;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_weather_main, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public int getItemCount() {
            return weatherResponses.size();
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            WeatherResponse weatherResponse = weatherResponses.get(position);

            holder.labelName.setText(weatherResponse.getName());
            holder.labelDesc.setText(weatherResponse.getWeather().get(0).getDescription());
            holder.labelTemp.setText(weatherResponse.getMain().getTemp().toString());

            holder.container.setOnClickListener((View v) -> {
                openWeatherDetail(weatherResponse);
            });
        }
    }

}



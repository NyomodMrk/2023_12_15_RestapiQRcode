package com.example.a2023_12_15_restapiqrcode;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListAdatok extends AppCompatActivity {
    private Button btn_list_megse;
    private Button btn_list_modositas;
    private EditText edittxt_list_jegy;
    private EditText edittxt_list_nev;
    private EditText edittxt_list_Id;
    private ListView listiew_adatok;
    private List<Person> people = new ArrayList<>();
    private String url;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_adatok);
        init();
        RequestTask task = new RequestTask(url,"GET");
        task.execute();
        btn_list_modositas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emberModositas();
            }
        });
        btn_list_megse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                urlapAlaphelyzetbe();
            }
        });
    }

    private void init() {
        btn_list_megse = findViewById(R.id.btn_list_megse);
        btn_list_modositas = findViewById(R.id.btn_list_modositas);
        edittxt_list_jegy = findViewById(R.id.edittxt_list_jegy);
        edittxt_list_nev = findViewById(R.id.edittxt_list_nev);
        listiew_adatok.setAdapter(new PersonAdapter());
        editor = sharedPreferences.edit();
        url = sharedPreferences.getString("api", "Nincs ilyen adat");
    }
    private class PersonAdapter extends ArrayAdapter<Person> {
        public PersonAdapter() {
            super(ListAdatok.this, R.layout.person_list_adapter, people);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.person_list_adapter, null, false);

            Person actualPerson = people.get(position);
            TextView txtview_person_nev = view.findViewById(R.id.txtview_person_nev);
            TextView txtview_person_jegy = view.findViewById(R.id.txtview_person_jegy);
            TextView txtview_person_szerkesztes = view.findViewById(R.id.txtview_person_szerkesztes);

            txtview_person_nev.setText(actualPerson.getName());
            txtview_person_jegy.setText(String.valueOf(actualPerson.getGrade()));

            txtview_person_szerkesztes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    edittxt_list_Id.setText(String.valueOf(actualPerson.getId()));
                    edittxt_list_nev.setText(actualPerson.getName());
                    edittxt_list_jegy.setText(actualPerson.getGrade());
                }
            });
            return view;
        }
    }
    private void emberModositas() {
        String nev = edittxt_list_nev.getText().toString();
        String jegy = edittxt_list_jegy.getText().toString();
        String idText = edittxt_list_Id.getText().toString();

        boolean valid = validacio();
        if (valid) {
            Toast.makeText(this, "Minden mezőt ki kell tölteni", Toast.LENGTH_SHORT).show();
        } else {
            int id =Integer.parseInt(idText);
            Person person = new Person(id, nev, jegy);
            Gson jsonConverter = new Gson();
            RequestTask task = new RequestTask(url + "/" + id, "PUT", jsonConverter.toJson(person));
            task.execute();
        }
    }
    private boolean validacio() {
        if (edittxt_list_nev.getText().toString().isEmpty() || edittxt_list_jegy.getText().toString().isEmpty())
            return true;
        else return false;
    }

    private void urlapAlaphelyzetbe() {
        edittxt_list_nev.setText("");
        edittxt_list_jegy.setText("");
        RequestTask task = new RequestTask(url, "GET");
        task.execute();
    }

    private class RequestTask extends AsyncTask<Void, Void, Response> {
        String requestUrl;
        String requestType;
        String requestParams;

        public RequestTask(String requestUrl, String requestType, String requestParams) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
            this.requestParams = requestParams;
        }

        public RequestTask(String requestUrl, String requestType) {
            this.requestUrl = requestUrl;
            this.requestType = requestType;
        }

        @Override
        protected Response doInBackground(Void... voids) {
            Response response = null;
            try {
                switch (requestType) {
                    case "GET":
                        response = RequestHandler.get(requestUrl);
                        break;
                    case "POST":
                        response = RequestHandler.post(requestUrl, requestParams);
                        break;
                }
            } catch (IOException e) {
                Toast.makeText(ListAdatok.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
            return response;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            Gson converter = new Gson();
            if (response.getResponseCode() >= 400) {
                Toast.makeText(ListAdatok.this, "Hiba történt a kérés feldolgozása során", Toast.LENGTH_SHORT).show();
                Log.d("onPostExecuteError: ", response.getResponseMessage());
            }
            switch (requestType) {
                case "GET":
                    Person[] peopleArray = converter.fromJson(response.getResponseMessage(), Person[].class);
                    people.clear();
                    people.addAll(Arrays.asList(peopleArray));
                    break;
                case "POST":
                    Person person = converter.fromJson(response.getResponseMessage(), Person.class);
                    people.add(0, person);
                    urlapAlaphelyzetbe();
                    break;

            }
        }
    }
}
package dev.carloszuil.herojourney.ui.fort;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import dev.carloszuil.herojourney.R;
import dev.carloszuil.herojourney.data.local.entities.Revelation;
import dev.carloszuil.herojourney.data.remote.ApiService;
import dev.carloszuil.herojourney.data.remote.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RevelationsFragment extends Fragment {

    private boolean errorShown = false;
    private Button buttonGetRevelation;
    private TextView textRevelation;
    private String selectedCategorie;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_revelations, container, false);

        buttonGetRevelation = root.findViewById(R.id.buttonGetRevelation);
        textRevelation = root.findViewById(R.id.textRevelation);

        buttonGetRevelation.setOnClickListener(v -> {
            Log.d("RevelationsFragment", "Botón clicado");
            getRevelation();
        });

        return root;
    }

    private void getRevelation(){

        ApiService apiService = RetrofitClient.getApiService();

        Call<Revelation> call = apiService.getRevelation("espiritual");
        call.enqueue(new Callback<Revelation>() {
            @Override
            public void onResponse(Call<Revelation> call, Response<Revelation> response) {
                Log.d("RevelationsFragment", "onResponse: " + response.code());
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("RevelationsFragment", "Cuerpo: " + response.body().getContent());
                    textRevelation.setText(response.body().getContent());
                } else {
                    Log.d("RevelationsFragment", "Respuesta no exitosa");
                    showToast(getString(R.string.no_revelation));
                    textRevelation.setText(R.string.no_revelation);
                }
            }

            @Override
            public void onFailure(Call<Revelation> call, Throwable t) {
                if (!isAdded()) return;
                if(!errorShown){
                    errorShown = true;
                    Log.e("RevelationsFragment", "onFailure: " + t.getMessage() + " ", t);
                    showToast(getString(R.string.no_communication));
                    textRevelation.setText(R.string.no_communication);
                    buttonGetRevelation.setEnabled(false);
                }
            }

        });
    }

    private void showToast(String msg) {
        // 2) Contexto seguro: usar getActivity() si existe
        if (getActivity() != null) {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }
}

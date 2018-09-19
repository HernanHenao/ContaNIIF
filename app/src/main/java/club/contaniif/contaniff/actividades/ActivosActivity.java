package club.contaniif.contaniff.actividades;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import club.contaniif.contaniff.R;
import club.contaniif.contaniff.adapter.AdapterActivos;
import club.contaniif.contaniff.adapter.AdapterSabias;
import club.contaniif.contaniff.entidades.ActivosVo;
import club.contaniif.contaniff.entidades.SabiasVo;
import club.contaniif.contaniff.entidades.VolleySingleton;

public class ActivosActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {


    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    RecyclerView obtenidos, disponible;
    String credenciales, correo;
    ArrayList<ActivosVo> listActivos, listaDisponible;
    ActivosVo activosVo;
    Dialog dialog;
    Button comprar;
    ImageView Imgactivo;
    AdapterActivos adapterActivos, adapterDisponible;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activos);


        obtenidos = findViewById(R.id.activosObtenidos);
        disponible = findViewById(R.id.activosDisponibles);
        dialog = new Dialog(this);
        listActivos = new ArrayList<>();
        listaDisponible = new ArrayList<>();
        cargarWebService();
    }

    private void cargarWebService() {
        cargarCredenciales();
        request = Volley.newRequestQueue(getApplication());
        String url = "https://" + getApplicationContext().getString(R.string.ip) + "activos.php?idusuario=" + correo;
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, this, this);
        request.add(jsonObjectRequest);
    }

    private void cargarCredenciales() {
        SharedPreferences preferences = this.getSharedPreferences("Credenciales", Context.MODE_PRIVATE);
        String credenciales = preferences.getString("correo", "No existe el valor");
        this.credenciales = credenciales;
        correo = credenciales;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getApplication(), "NO se pudo Consultar:" + error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onResponse(JSONObject response) {
        JSONArray json = response.optJSONArray("activos");
        try {
            JSONObject jsonObject = null;
            for (int i = 0; i < json.length(); i++) {
                jsonObject = json.getJSONObject(i);
                activosVo = new ActivosVo();
                activosVo.setId(jsonObject.optString("id"));
                activosVo.setNombre(jsonObject.optString("nombre"));
                activosVo.setDescripcion(jsonObject.optString("descripcion"));
                activosVo.setValor(jsonObject.optString("valor"));
                activosVo.setDescuento(jsonObject.optString("descuento"));
                activosVo.setEstado(jsonObject.optString("estado"));
                if (jsonObject.optString("estado").equals("Tiene")) {
                    listActivos.add(activosVo);
                } else {
                    listaDisponible.add(activosVo);
                }
            }

            adapterActivos = new AdapterActivos(listActivos, getApplicationContext());
            obtenidos.setAdapter(adapterActivos);

            adapterDisponible = new AdapterActivos(listaDisponible, getApplicationContext());
            disponible.setAdapter(adapterDisponible);

            adapterDisponible.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cargarVentana(listaDisponible.get(disponible.getChildAdapterPosition(view)));

                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "No se ha podido establecer conexión con el servidor" +
                    " " + response, Toast.LENGTH_LONG).show();
        }
    }

    private void cargarVentana(ActivosVo activos) {
        ActivosVo vo=activos;
        Toast.makeText(getApplicationContext(), ""+vo.getNombre(), Toast.LENGTH_LONG).show();
        TextView titulo,descrip,valor;
        dialog.setContentView(R.layout.popup_activos);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        comprar = dialog.findViewById(R.id.btnComprar);
        titulo=dialog.findViewById(R.id.tituloPopupActivo);
        descrip=dialog.findViewById(R.id.descripActivoPopup);
        valor=dialog.findViewById(R.id.precioActivoPopup);
        Imgactivo=dialog.findViewById(R.id.imagenPopupActivo);

        titulo.setText(""+vo.getNombre());
        descrip.setText(""+vo.getDescripcion());
        valor.setText(""+vo.getValor());

        cargarImgGeneral(vo.getId());
        comprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        dialog.show();
    }

    private void cargarImgGeneral(String rutaImagen) {
        String ip=getApplicationContext().getString(R.string.imgRendimiento);

        final String urlImagen="https://"+ip+"activos/"+rutaImagen+".png";
        ImageRequest imageRequest=new ImageRequest(urlImagen, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                Imgactivo.setImageBitmap(response);
            }
        }, 0, 0, ImageView.ScaleType.CENTER, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Error al cargar la imagen" + urlImagen, Toast.LENGTH_LONG).show();
            }
        });
        request.add(imageRequest);
    }
}

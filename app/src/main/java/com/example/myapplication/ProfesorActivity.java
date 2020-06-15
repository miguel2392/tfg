package com.example.myapplication;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

/**
 * Actividad que muestra la lista de asignaturas de un profesor. Si se clica sobre una asignatura se
 * lanza la {@link PresentacionActivity}
 */
public class ProfesorActivity extends AppCompatActivity {

    private static String EXTRA_NAME = "EXTRA_NAME";
    public static void startActivity(Context context, String name) {

        Intent intent = new Intent(context, ProfesorActivity.class);
        intent.putExtra(EXTRA_NAME, name);
        context.startActivity(intent);
    }

    private LinearLayout mainContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabaseManager appDatabaseManager = new AppDatabaseManager();
        AppAuthManager appAuthManager = new AppAuthManager();
        setContentView(R.layout.activity_profesor);
        String name = getIntent().getStringExtra(EXTRA_NAME);

        mainContainer = findViewById(R.id.asignaturas_container);
        TextView nameText = findViewById(R.id.teacher_name);
        setTitle(name);
        nameText.setText("Asignaturas");
        String uuid = appAuthManager.getUserId();
        if (uuid == null) {
            startActivity(new Intent(this, AuthenticationActivity.class));
            finish();
        }

        appDatabaseManager.getAsignaturas(true, new AppDatabaseManager.AsiganturasListener() {
            @Override
            public void onAsignaturasRecevied(List<Asignatura> asignaturas) {
                displayAsignaturas(asignaturas);
            }
        });
    }

    private void displayAsignaturas(List<Asignatura> asignaturas) {
        mainContainer.removeAllViews();
        for (Asignatura asignatura : asignaturas) {
            AsignaturaItemView itemView = new AsignaturaItemView(this);
            itemView.bindData(asignatura);
            itemView.setOnAsignaturaClickListener(new AsignaturaClickListener() {
                @Override
                public void onAsignaturaClick(Asignatura asignatura) {

                    PresentacionActivity.startActivity(ProfesorActivity.this,asignatura.id,asignatura.name);
                }
            });
            mainContainer.addView(itemView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actionlogout,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_logout){
            showLogOutDialog();
        }
        return true;
    }

    private void showLogOutDialog(){
        new AlertDialog.Builder(this)
                .setMessage("¿Quieres cerrar sesión?")
                .setPositiveButton("Cerrar sesión", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        signOut();
                    }
                })
                .setNegativeButton("Volver", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void signOut() {
        AppAuthManager appAuthManager = new AppAuthManager();
        appAuthManager.signOut();
        Intent intentSignIn = new Intent(this, AuthenticationActivity.class);
        startActivity(intentSignIn);
        finish();
    }
}

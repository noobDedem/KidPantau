package tugas.besar.pbp.kidpantau.registrasi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import lab.praktikum.pbp.kidpantau.R;

public class Option extends AppCompatActivity {
    Button create, join;
    public static Activity option;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        option = this;
        create = (Button)findViewById(R.id.create);
        join = (Button)findViewById(R.id.join);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistrasiNF.class);
                startActivity(intent);
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistrasiEF.class);
                startActivity(intent);
            }
        });
    }
}

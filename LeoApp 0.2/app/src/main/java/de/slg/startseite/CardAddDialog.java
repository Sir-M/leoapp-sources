package de.slg.startseite;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;

import de.slg.leoapp.R;

class CardAddDialog extends AlertDialog {

    private MainActivity context;
    private ImageButton[] buttons;
    private CardType type;

    CardAddDialog(@NonNull MainActivity context) {
        super(context);

        this.context = context;

    }

    @Override
    public void onCreate(Bundle b) {

        super.onCreate(b);
        setContentView(R.layout.dialog_add_card);

        buttons = new ImageButton[]{
                (ImageButton) findViewById(R.id.imageButton1),
                (ImageButton) findViewById(R.id.imageButton2),
                (ImageButton) findViewById(R.id.imageButton3),
                (ImageButton) findViewById(R.id.imageButton4),
                (ImageButton) findViewById(R.id.imageButton5),
                (ImageButton) findViewById(R.id.imageButton6),
                (ImageButton) findViewById(R.id.imageButton7),
                (ImageButton) findViewById(R.id.imageButton8),
        };

        initOptions();
        initSendButton();

    }

    private void initOptions() {


        for (ImageButton b : buttons) {

            final ImageButton copy = b;

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    greyOut();
                    copy.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
                    findViewById(R.id.buttonDialog2).setEnabled(true);
                    type = CardType.valueOf(String.valueOf(copy.getTag()));
                }
            });

        }

    }

    private void greyOut() {

        for (ImageButton b : buttons) {

            b.setColorFilter(Color.GRAY);

        }

    }


    private void initSendButton() {

        findViewById(R.id.buttonDialog1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        findViewById(R.id.buttonDialog2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.addCard(type);
                dismiss();
            }
        });

    }


}

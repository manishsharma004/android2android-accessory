package de.quandoo.android2androidaccessory;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public abstract class BaseChatActivity extends ActionBarActivity {

    @BindView(R.id.content_text)
    TextView contentTextView;

    @BindView(R.id.input_edittext)
    EditText input;

    @OnClick(R.id.send_button)
    public void onButtonClick() {
        final String inputString = input.getText().toString();
        if (inputString.length() == 0) {
            return;
        }

        sendString(inputString);
        printLineToUI(getString(R.string.local_prompt) + inputString);
        input.setText("");
    }

    protected abstract void sendString(final String string);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

    }

    protected void printLineToUI(final String line) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                contentTextView.setText(contentTextView.getText() + "\n" + line);
            }
        });
    }

}

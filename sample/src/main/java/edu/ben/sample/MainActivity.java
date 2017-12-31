package edu.ben.sample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.toString();

    @BindView(R.id.contacts_button) Button mContactChipButton;
    @BindView(R.id.fragment) Button mFragmentChipsButton;
    private int mStackLevel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // butter knife
        ButterKnife.bind(this);

        mContactChipButton.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, ContactChipActivity.class));
        });

        mFragmentChipsButton.setOnClickListener(view -> {
            // Begin the transaction
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            // Replace the contents of the container with the new fragment
            ft.replace(R.id.container, new ContactChipFragment());
            // or ft.add(R.id.your_placeholder, new FooFragment());
            // Complete the changes added above
            ft.commit();
        });
    }

    @OnClick(R.id.dialog_fragment)
    public void showDialog() {
        mStackLevel++;

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");

        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        ChipFragment newFragment = ChipFragment.newInstance(mStackLevel);
        newFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.Dialog_FullScreen);
        newFragment.show(ft, "ok");
    }
}

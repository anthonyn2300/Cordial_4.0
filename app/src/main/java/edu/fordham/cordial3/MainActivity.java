package edu.fordham.cordial3;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 111;
    TextView nameTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameTextView = findViewById(R.id.nameTextView);

        createSignInIntent();
    }

    void createSignInIntent()
    {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
        );

        startActivityForResult(
                AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
        RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN)
        {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK)
            {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                nameTextView.setText(user.getDisplayName());
            }
            else
            {

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.signOutMenuItem)
        {
            signOut();
            return true;
        }
        if (item.getItemId() == R.id.calendarMenuItem)
        {
            return true;
        }
        if (item.getItemId() == R.id.searchMenuItem)
        {
            return true;
        }
        if (item.getItemId() == R.id.messagesMenuItem)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private  void signOut()
    {

    }

    public void AddCalendarEvent (View view){
            Calendar calendarEvent = Calendar.getInstance();
            Intent i = new Intent(Intent.ACTION_EDIT);
            i.setType("vnd.android.cursor.item/event");
            i.putExtra("beginTime", calendarEvent.getTimeInMillis());
            i.putExtra("allDay", true);
            i.putExtra("rule", "FREQ=YEARLY");
            i.putExtra("endTime", calendarEvent.getTimeInMillis() + 60 * 60 * 1000);
            i.putExtra("title", "Calendar Event");
            startActivity(i);
        }
}

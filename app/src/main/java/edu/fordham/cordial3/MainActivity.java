package edu.fordham.cordial3;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String MESSAGES_CHILD = "messages";
    public static final String USERS_CHILD = "users";
    private static final int RC_SIGN_IN = 111;
    TextView nameTextView;
    private static final int RC_IMAGE = 234;
    RecyclerView messageList;
    EditText messageEditText;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        messageEditText = findViewById(R.id.messageEditText);
        messageList = findViewById(R.id.messageList);

        nameTextView = findViewById(R.id.nameTextView);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null)
        {
            nameTextView.setText(user.getDisplayName());
        }
        else {
            createSignInIntent();
        }
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
                Toast.makeText(this, "Sign in failed", Toast.LENGTH_LONG).show();
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
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                            createSignInIntent();
                    }
                });
    }

    public void send(View view)
    {
        String text = messageEditText.getText().toString().trim();

        //bug
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Message message = new Message(text, user.getUid());
        mDatabase.child("messages").push().setValue(message);
        messageEditText.setText("");
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

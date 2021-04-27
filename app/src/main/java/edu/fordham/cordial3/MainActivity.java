package edu.fordham.cordial3;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
    FirebaseRecyclerAdapter<Messages, MessageViewHolder> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        messageEditText = findViewById(R.id.messageEditText);
        messageList = findViewById(R.id.messageList);

        FirebaseRecyclerOptions<Messages> options =
                new FirebaseRecyclerOptions.Builder<Messages>()
                .setQuery(FirebaseDatabase.getInstance()
                .getReference()
                .child(MESSAGES_CHILD), Messages.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Messages, MessageViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Messages msg) {
                holder.bindMessage(msg);
            }

            @NonNull
            @Override
            public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
                return new MessageViewHolder(view);
            }
        };
        messageList.setLayoutManager(new LinearLayoutManager(this));
        messageList.setAdapter(adapter);

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
                Intent i = new Intent(MainActivity.this, CalandarTab.class);
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
        if (item.getItemId() == R.id.forumMenuItem)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void signOut()
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

        //bug
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Messages message = new
                Messages(messageEditText.getText().toString().trim(),
                user.getUid());
        mDatabase.child(MESSAGES_CHILD).push().setValue(message);
        messageEditText.setText("");
    }

}

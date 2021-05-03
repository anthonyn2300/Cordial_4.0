package edu.fordham.cordial3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

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
    FirebaseAuth firebaseAuth;



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
        } //added extra for image
        else if (requestCode == RC_IMAGE)
        {
            if (requestCode == RESULT_OK && data != null)
            {
                final Uri uri = data.getData();
                Log.d("mobdev", "Uri" + uri.toString());

                String key = mDatabase.child(MESSAGES_CHILD).push().getKey();

                final FirebaseUser user = firebaseAuth.getCurrentUser();
                StorageReference storageReference =
                        FirebaseStorage.getInstance()
                            .getReference(user.getUid())
                            .child(key)
                            .child(uri.getLastPathSegment());

                storageReference.putFile(uri)
                        .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            Log.i("mobdev", "Update message " + key + ": " + uri);
                                            final FirebaseUser user = firebaseAuth.getCurrentUser();
                                            Messages msg = new Messages(null, user.getUid(), uri.toString());
                                            mDatabase.child(MESSAGES_CHILD)
                                                    .child(key)
                                                    .setValue(msg);
                                        }
                                    });


                            }
                        });
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
        if (item.getItemId() == R.id.signOut)
        {
            signOut();
            return true;
        }
        if (item.getItemId() == R.id.calendarMenuItem)
        {
            Intent i = new Intent(MainActivity.this, CalandarTab.class);
            startActivity(i);
            return true;
        }
        if (item.getItemId() == R.id.searchMenuItem)
        {
            Intent i = new Intent(MainActivity.this, Search.class);
            startActivity(i);
            return true;
        }
        if (item.getItemId() == R.id.forumMenuItem)
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        }
        if (item.getItemId() == R.id.profileMenuItem)
        {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
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
                user.getUid(), user.getDisplayName());
        mDatabase.child(MESSAGES_CHILD).push().setValue(message);
        messageEditText.setText("");
    }

    public void addMessage(View view)
    {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, RC_IMAGE);
    }

}

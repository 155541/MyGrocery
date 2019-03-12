package revolhope.splanes.com.mygrocery.helpers.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import revolhope.splanes.com.mygrocery.data.model.item.Item;
import revolhope.splanes.com.mygrocery.data.model.User;
import revolhope.splanes.com.mygrocery.helpers.repository.AppRepository;

public class AppFirebase {

    private static final String db_user = "db/db-user";
    private static final String db_item = "db/db-data/db-pending-item";
    private static final String db_notification = "db/db-data/notification";
    private static final String db_historic = "db/db-data/historic";

    public interface OnComplete {
        void taskCompleted(boolean success, Object... parameters);
    }

    private static AppFirebase instance;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser mUser;
    private FirebaseDatabase firebaseDatabase;

    private AppFirebase() {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseDatabase = FirebaseDatabase.getInstance();
    }

    public static AppFirebase getInstance() {
        if (instance == null) {
            instance = new AppFirebase();
        }
        return instance;
    }



    public void pushUser(@NonNull User newUser, @NonNull final OnComplete onComplete) {

        DatabaseReference dbRef = firebaseDatabase.getReference(db_user);
        dbRef.push().setValue(newUser, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError,
                                   @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    onComplete.taskCompleted(true);
                } else {
                    onComplete.taskCompleted(false, databaseError.getMessage());
                }
            }
        });
    }

    public void fetchUser(@NonNull final String id, @NonNull final OnComplete onComplete) {
        DatabaseReference dbRef = firebaseDatabase.getReference(db_user);
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    u = snapshot.getValue(User.class);
                    if (u != null && id.equals(u.getId())) {
                        onComplete.taskCompleted(true, u);
                        return;
                    }
                }
                onComplete.taskCompleted(false);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onComplete.taskCompleted(false, databaseError.getMessage());
            }
        });
    }

    public void pushItem(@NonNull final Item newItem, @NonNull final OnComplete onComplete) {
        resolveEmails(newItem.getUsersTarget(), new OnComplete() {
            @Override
            public void taskCompleted(boolean success, Object... parameters) {
                if (success) {
                    final String[] targetIds = (String[]) parameters;
                    final DatabaseReference dbRef = firebaseDatabase.getReference(db_item);
                    dbRef.child(AppRepository.getAppUser().getId()).push().setValue(newItem,
                                                                 new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError,
                                               @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                for (String id : targetIds) {
                                    dbRef.child(id).push().setValue(newItem, null);
                                }
                                onComplete.taskCompleted(true);
                            } else {
                                onComplete.taskCompleted(false, databaseError.getMessage());
                            }
                        }
                    });
                }
                else {
                    onComplete.taskCompleted(false, parameters);
                }
            }
        });
    }
    
    private void resolveEmails(final List<String> emails, @NonNull final OnComplete onComplete) {
        final List<String> ids = new ArrayList<>();
        DatabaseReference dRef = firebaseDatabase.getReference(db_user);
        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User u;
                int count = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (count == emails.size()) break;
                    u = snapshot.getValue(User.class);
                    if (u != null && emails.contains(u.getEmail())) {
                        count++;
                        ids.add(u.getId());
                    }
                }
                onComplete.taskCompleted(true, (Object[])(ids.toArray(new String[0])));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onComplete.taskCompleted(false, databaseError.getMessage());
            }
        });
    }
    
    public void fetchItems(@NonNull final String userId, @NonNull final OnComplete onComplete) {
        DatabaseReference dRef = firebaseDatabase.getReference(db_item);
        dRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId)) {
                    DataSnapshot userRef = dataSnapshot.child(userId);
                    Item[] items = new Item[(int)userRef.getChildrenCount()];
                    int i = 0;
                    for (DataSnapshot snap : userRef.getChildren()) {
                        items[i] = (snap.getValue(Item.class));
                        i++;
                    }
                    onComplete.taskCompleted(true, (Object[]) items);
                }
                else onComplete.taskCompleted(true);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                onComplete.taskCompleted(false, databaseError.getMessage());
            }
        });
    }

    // ???
    public boolean isCurrentlySignedIn() {
        return firebaseAuth.getCurrentUser() != null;
    }

    public void signUp(String email, String password, final OnComplete onComplete) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mUser = firebaseAuth.getCurrentUser();
                            onComplete.taskCompleted(true, mUser);
                        } else {
                            onComplete.taskCompleted(false, task.getException());
                        }
                    }
                });
    }

    public void signIn(String email, String password, final OnComplete onComplete) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mUser = firebaseAuth.getCurrentUser();
                            onComplete.taskCompleted(true, mUser);
                        }
                        else {
                            onComplete.taskCompleted(false, task.getException());
                        }
                    }
                });
    }

    public void signOut() {
        firebaseAuth.signOut();
    }

    public void findItems(int filter, final OnComplete onComplete) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();

    }
}

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import revolhope.splanes.com.mygrocery.data.model.ItemNotification;
import revolhope.splanes.com.mygrocery.data.model.User;
import revolhope.splanes.com.mygrocery.data.model.item.Item;

public class AppFirebase {

    private static final String db_user = "db/db-user";
    private static final String db_item = "db/db-data/db-pending-item";
    private static final String db_notification = "db/db-data/db-notification";
    private static final String db_historic = "db/db-data/db-historic";

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

//**************************************************************************************************
//  Account Manage
//**************************************************************************************************

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

//**************************************************************************************************
//  User
//**************************************************************************************************

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

//**************************************************************************************************
//  Item
//**************************************************************************************************

    public void pushItem(@NonNull final Item newItem, final boolean isUpdate,
                         @NonNull final String userId, @NonNull final OnComplete onComplete) {
        if (!isUpdate) {
            newItem.setIsSeen(1);
            newItem.setIsReminderSet(1);
        }
        resolveEmails(newItem.getUsersTarget(), new OnComplete() {
            @Override
            public void taskCompleted(boolean success, Object... parameters) {
                if (success) {
                    final String[] targetIds = (String[]) parameters;
                    final DatabaseReference dbRef = firebaseDatabase.getReference(db_item);
                    dbRef.child(userId).push()
                            .setValue(newItem, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError,
                                               @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                if (!isUpdate) {
                                    newItem.setIsSeen(0);
                                    newItem.setIsReminderSet(0);
                                }
                                for (String id : targetIds) {
                                    dbRef.child(id).push().setValue(newItem, null);
                                }
                                onComplete.taskCompleted(true);
                            } else {
                                onComplete.taskCompleted(false, databaseError.getMessage());
                            }
                        }
                    });
                    final DatabaseReference dbRefNot = firebaseDatabase.getReference(db_notification);
                    ItemNotification itemNotification;
                    for (String id : targetIds) {
                        if (!id.equals(userId)) {
                            itemNotification = buildItemNotification(newItem, isUpdate);
                            dbRefNot.child(id).push().setValue(itemNotification, null);
                        }
                    }

                }
                else {
                    onComplete.taskCompleted(false, parameters);
                }
            }
        });
    }

    public void pushItemForMe(@NonNull final Item item, @NonNull final String userId) {
        item.setIsSeen(1);
        DatabaseReference dbRef = firebaseDatabase.getReference(db_item).child(userId);
        dbRef.push().setValue(item, null);
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

    public void deleteItem(@NonNull final String itemId, @NonNull final String userId,
                           @NonNull final OnComplete onComplete) {
        final DatabaseReference dRef = firebaseDatabase.getReference(db_item);
        dRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Item aux;
                for (DataSnapshot dataItem : dataSnapshot.getChildren()) {
                    aux = dataItem.getValue(Item.class);
                    if (dataItem.getKey() != null && aux != null && aux.getId().equals(itemId)) {
                        dRef.child(userId).child(dataItem.getKey()).removeValue();
                        resolveEmails(aux.getUsersTarget(), new OnComplete() {
                            @Override
                            public void taskCompleted(boolean success, Object... parameters) {
                                if (success) {
                                    for (final String id : (String[]) parameters) {
                                        dRef.child(id).addListenerForSingleValueEvent(
                                                new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Item aux;
                                                for (DataSnapshot dataItem : dataSnapshot.getChildren()){
                                                    aux = dataItem.getValue(Item.class);
                                                    if (dataItem.getKey() != null && aux != null && aux.getId().equals(itemId)) {
                                                        dRef.child(id).child(dataItem.getKey()).removeValue();
                                                        break;
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {}
                                        });
                                    }
                                }
                            }
                        });
                        onComplete.taskCompleted(true);
                        break;
                    }
                }
                onComplete.taskCompleted(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }

    public void deleteMyItems(@NonNull final String userId) {
        final DatabaseReference dRef = firebaseDatabase.getReference(db_item).child(userId);
        dRef.removeValue();
    }

//**************************************************************************************************
//  Historic
//**************************************************************************************************

//**************************************************************************************************
//  Private
//**************************************************************************************************
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

    private ItemNotification buildItemNotification(Item item, boolean isUpdate) {
        ItemNotification itemNotification = new ItemNotification();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE);
        itemNotification.setItemName(item.getItemName());
        if(isUpdate) {
            itemNotification.setAction("Article modificat");
        } else {
            itemNotification.setAction("Nou article afegit");
        }
        switch (item.getPriority()) {
            case 0:
                itemNotification.setPriority("Prioritat: Alta");
                break;
            case 1:
                itemNotification.setPriority("Prioritat: Normal");
                break;
            case 2:
                itemNotification.setPriority("Prioritat: Baixa");
                break;
        }
        switch (item.getCategory()) {
            case 0:
                itemNotification.setCategory("Categoria: Sense categoria");
                break;
            case 1:
                itemNotification.setCategory("Categoria: Supermercat");
                break;
            case 2:
                itemNotification.setCategory("Categoria: Ferreteria");
                break;
            case 3:
                itemNotification.setCategory("Categoria: Electrònica");
                break;
            case 4:
                itemNotification.setCategory("Categoria: Aniversari");
                break;
            case 5:
                itemNotification.setCategory("Categoria: Altres casa");
                break;
        }
        itemNotification.setAmount("Quantitat: " + item.getAmount());
        itemNotification.setDescription(
                "Recordatori? " + (item.getDateReminder() == 0L ? "No" : "Si") + "\n" +
                "Data i hora: " + sdf.format(Calendar.getInstance().getTime()));
        return itemNotification;
    }
}

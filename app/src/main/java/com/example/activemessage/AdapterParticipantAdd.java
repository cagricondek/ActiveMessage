package com.example.activemessage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.ColorSpace;
import android.icu.util.TimeZone;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class AdapterParticipantAdd extends RecyclerView.Adapter<AdapterParticipantAdd.HolderParticipantAdd> {

    private Context context;
    private ArrayList<ModelUser> userList;
    private String groupId,myGroupRole;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String currentUserID;
    public AdapterParticipantAdd(Context context, ArrayList<ModelUser> userList, String groupId, String myGroupRole) {
        this.context = context;
        this.userList = userList;
        this.groupId = groupId;
        this.myGroupRole = myGroupRole;
    }

    @NonNull
    @Override
    public HolderParticipantAdd onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.row_participant_add,parent,false);

        return new HolderParticipantAdd(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderParticipantAdd holder, int position) {
        ModelUser modelUser=userList.get(position);
        String name=modelUser.getName();
        String email=modelUser.getEmail();
        String image=modelUser.getImage();
        String uid=modelUser.getUid();

        holder.nameTv.setText(name);
        holder.emailTv.setText(email);
       try{
           Picasso.get().load(image).placeholder(R.drawable.profile).into(holder.avatarIv);
       }
       catch (Exception e)
       {
           holder.avatarIv.setImageResource(R.drawable.profile);
       }

        checkIfAlreadyExists(modelUser,holder);
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
               ref.child(groupId).child("Participants").child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       if (snapshot.exists())
                       {
                           String hisPreviousRole= ""+snapshot.child("role").getValue();
                           String [] options;
                           AlertDialog.Builder builder=new AlertDialog.Builder(context);
                           builder.setTitle("Rol Seçiniz");
                           if(myGroupRole.equals("creator"))
                           {
                               if (hisPreviousRole.equals("admin"))
                               {
                                options=new String[]{"Grup Yöneticisi Sil","Üyeyi Sil"};
                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which==0)
                                        {
                                        removeAdmin(modelUser);
                                        }
                                        else
                                            {
                                            removeParticipant(modelUser);
                                        }
                                    }
                                }).show();
                               }
                               else if(hisPreviousRole.equals("participant"))
                               {
                                   options=new String[]{"Yönetici Yap","Üyeyi Sil"};
                                   builder.setItems(options, new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           if (which==0)
                                           {
                                               makeAdmin(modelUser);
                                           }
                                           else
                                           {
                                               removeParticipant(modelUser);
                                           }
                                       }
                                   }).show();

                               }
                           }
                           else if(myGroupRole.equals("admin"))
                           {
                               if (hisPreviousRole.equals("creator")){
                                   Toast.makeText(context, "Grup Yöneticisi", Toast.LENGTH_SHORT).show();
                               }
                               else if (hisPreviousRole.equals("admin")){
                                   options=new String[]{"Grup Yöneticisi Sil","Üyeyi Sil"};
                                   builder.setItems(options, new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           if (which==0)
                                           {
                                               removeAdmin(modelUser);
                                           }
                                           else
                                           {
                                               removeParticipant(modelUser);
                                           }
                                       }
                                   }).show();
                               }
                               else if (hisPreviousRole.equals("participant")){
                                   options=new String[]{"Grup Yöneticisi Yap","Üyeyi Sil"};
                                   builder.setItems(options, new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int which) {
                                           if (which==0)
                                           {
                                               makeAdmin(modelUser);
                                           }
                                           else
                                           {
                                               removeParticipant(modelUser);
                                           }
                                       }
                                   }).show();
                               }
                           }
                       }
                       else
                           {
                        AlertDialog.Builder builder= new AlertDialog.Builder(context);
                        builder.setTitle("Üye Ekle")
                                .setMessage("Kullanıcıyı Gruba Eklemek İstiyormusunuz?")
                                .setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    addParticipant(modelUser);
                                    }
                                }).setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                           }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });
           }
       });
    }

    private void addParticipant(ModelUser modelUser)
    {
        String timestamp=""+ System.currentTimeMillis();
        HashMap<String,String>hashMap=new HashMap<>();
        hashMap.put("uid",modelUser.getUid());
        hashMap.put("role","participant");
        hashMap.put("timestamp",""+timestamp);
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUid()).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Kullanıcı Eklendi.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void makeAdmin(ModelUser modelUser)
    {
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("role","admin");
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Grup Yöneticisi Oluşturuldu", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void removeParticipant(ModelUser modelUser)
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUid()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void removeAdmin(ModelUser modelUser)
    {
        HashMap<String,Object>hashMap=new HashMap<>();
        hashMap.put("role","participant");
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Participants").child(modelUser.getUid()).updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Grup Yöneticisi Silindi ", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkIfAlreadyExists(ModelUser modelUser, HolderParticipantAdd holder) {

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserID = mAuth.getCurrentUser().getUid();
        ref.child(groupId).child("Participants").child(currentUserID).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.exists()){
                String hisRole=""+snapshot.child("role").getValue();
                holder.statusTv.setText(hisRole);
            }
            else
                {
                    holder.statusTv.setText("");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class HolderParticipantAdd extends RecyclerView.ViewHolder{

        private ImageView avatarIv;
        private TextView nameTv,emailTv,statusTv;



        public HolderParticipantAdd(@NonNull View itemView) {
            super(itemView);
            avatarIv=itemView.findViewById(R.id.avatarIv);
            nameTv=itemView.findViewById(R.id.nameTv);
            emailTv=itemView.findViewById(R.id.emailTv);
            statusTv=itemView.findViewById(R.id.statusTv);
        }
    }
}

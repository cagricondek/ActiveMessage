package com.example.activemessage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private List<Messages> userMessagesList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    public MessageAdapter(List<Messages> userMessagesList)
    {
        this.userMessagesList=userMessagesList;
    }


    public class MessageViewHolder extends RecyclerView.ViewHolder
    {
        public TextView senderMessageText,receiverMessageText;
        public CircleImageView receiverProfileImage;
        public ImageView messageSenderPicture,messageReceiverPicture;



        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessageText=(TextView) itemView.findViewById(R.id.sender_messsage_text);
            receiverMessageText=(TextView) itemView.findViewById(R.id.receiver_message_text);
            receiverProfileImage=(CircleImageView) itemView.findViewById(R.id.message_profile_image);
            messageReceiverPicture=itemView.findViewById(R.id.message_receiver_image_view);
            messageSenderPicture=itemView.findViewById(R.id.message_sender_image_view);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View view= LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_messages_layout ,viewGroup,false);
        mAuth=FirebaseAuth.getInstance();
        return  new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int position)
    {
        String messageSenderId=mAuth.getCurrentUser().getUid();
        Messages messages=userMessagesList.get(messageViewHolder.getAdapterPosition());
        String fromUserID=messages.getFrom();
        String fromMessageType=messages.getType();

        usersRef=FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.hasChild("profileImage"))
                {
                    String receiverImage= snapshot.child("profileImage").getValue().toString();
                    Picasso.get().load(receiverImage).placeholder(R.drawable.profile).into(messageViewHolder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });

        messageViewHolder.receiverMessageText.setVisibility(View.GONE);
        messageViewHolder.receiverProfileImage.setVisibility(View.GONE);
        messageViewHolder.senderMessageText.setVisibility(View.GONE);
        messageViewHolder.messageSenderPicture.setVisibility(View.GONE);
        messageViewHolder.messageReceiverPicture.setVisibility(View.GONE);
        if (fromMessageType.equals("text"))
        {
            if (fromUserID.equals(messageSenderId))
            {
                messageViewHolder.senderMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
                messageViewHolder.senderMessageText.setText(messages.getMessage() + "\n \n" + messages.getDate()+"-"+messages.getTime());

            }
            else
            {

                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
                messageViewHolder.receiverMessageText.setText(messages.getMessage() + "\n \n" + messages.getDate()+"-"+messages.getTime());
            }
        }
        else if (fromMessageType.equals("image"))
        {
            if (fromUserID.equals(messageSenderId))
            {
                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageSenderPicture);

            }
            else
            {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(messageViewHolder.messageReceiverPicture);
            }
        }
        else if (fromMessageType.equals("pdf") || fromMessageType.equals("docx"))
        {
            if (fromUserID.equals(messageSenderId))
            {
                messageViewHolder.messageSenderPicture.setVisibility(View.VISIBLE);
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/activemessage-37887.appspot.com/o/Image%20Files%2FPDF.png?alt=media&token=39f5e251-d935-41ed-bfe4-b8166a4860ad")
                        .into(messageViewHolder.messageSenderPicture);

            }
            else
            {
                messageViewHolder.receiverProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.messageReceiverPicture.setVisibility(View.VISIBLE);
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/activemessage-37887.appspot.com/o/Image%20Files%2Fword.png?alt=media&token=5816b003-e59f-428e-818e-abd15606477a")
                        .into(messageViewHolder.messageReceiverPicture);

            }
        }




        if (fromUserID.equals(messageSenderId))
        {
            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (userMessagesList.get(messageViewHolder.getAdapterPosition()).getType().equals("pdf")||userMessagesList.get(messageViewHolder.getAdapterPosition()).getType().equals("docx"))
                    {
                        CharSequence options[]= new CharSequence[]
                                {
                                        "Benden Sil",
                                        "İndir ve Görüntüle",
                                        "İptal",
                                        "Herkes için Sil"
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Mesajı Sil");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if (i==0)
                                {
                                    deleteSentMessage(messageViewHolder.getAdapterPosition(), messageViewHolder);
                                    Intent intent =new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    intent.putExtra("url",userMessagesList.get(messageViewHolder.getAdapterPosition()).getMessage());
                                }
                                else if (i==1)
                                {
                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(messageViewHolder.getAdapterPosition()).getMessage()));
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }
                                else  if (i==3)
                                {
                                    deleteMessageForEveryOne(messageViewHolder.getAdapterPosition(), messageViewHolder);
                                    Intent intent =new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    intent.putExtra("url",userMessagesList.get(messageViewHolder.getAdapterPosition()).getMessage());
                                }
                            }
                        });
                        builder.show();
                    }
                    else if (userMessagesList.get(messageViewHolder.getAdapterPosition()).getType().equals("text"))
                    {
                        CharSequence options[]= new CharSequence[]
                                {
                                        "Benden Sil",
                                        "İptal",
                                        "Herkes için Sil"
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Mesajı Sil");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if (i==0)
                                {
                                    deleteSentMessage(messageViewHolder.getAdapterPosition(), messageViewHolder);
                                    Intent intent =new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    intent.putExtra("url",userMessagesList.get(messageViewHolder.getAdapterPosition()).getMessage());
                                }
                                else  if (i==2)
                                {
                                    deleteMessageForEveryOne(messageViewHolder.getAdapterPosition(), messageViewHolder);
                                    Intent intent =new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    intent.putExtra("url",userMessagesList.get(messageViewHolder.getAdapterPosition()).getMessage());
                                }

                            }
                        });
                        builder.show();
                    }
                    else   if (userMessagesList.get(messageViewHolder.getAdapterPosition()).getType().equals("image"))
                    {
                        CharSequence options[]= new CharSequence[]
                                {
                                        "Benden Sil",
                                        "Resmi Görüntüle",
                                        "İptal",
                                        "Herkes için Sil"
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Mesajı Sil");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if (i==0)
                                {
                                    deleteSentMessage(messageViewHolder.getAdapterPosition(), messageViewHolder);
                                    Intent intent =new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    intent.putExtra("url",userMessagesList.get(messageViewHolder.getAdapterPosition()).getMessage());
                                }
                                else if (i==1)
                                {
                                    Intent intent =new Intent(messageViewHolder.itemView.getContext(),ImageViewerActivity.class);
                                    intent.putExtra("url",userMessagesList.get(messageViewHolder.getAdapterPosition()).getMessage());
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }

                                else  if (i==3)
                                {
                                    deleteMessageForEveryOne(messageViewHolder.getAdapterPosition(), messageViewHolder);
                                    Intent intent =new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    intent.putExtra("url",userMessagesList.get(messageViewHolder.getAdapterPosition()).getMessage());
                                }
                            }
                        });
                        builder.show();
                    }

                }
            });
        }
        else
        {
            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (userMessagesList.get(messageViewHolder.getAdapterPosition()).getType().equals("pdf")||userMessagesList.get(messageViewHolder.getAdapterPosition()).getType().equals("docx"))
                    {
                        CharSequence options[]= new CharSequence[]
                                {
                                        "Benden Sil",
                                        "İndir ve Görüntüle",
                                        "İptal",
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Mesajı Sil");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if (i==0)
                                {
                                    deleteReceiveMessage(messageViewHolder.getAdapterPosition(), messageViewHolder);
                                    Intent intent =new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    intent.putExtra("url",userMessagesList.get(messageViewHolder.getAdapterPosition()).getMessage());
                                }
                                else if (i==1)
                                {
                                    Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(userMessagesList.get(messageViewHolder.getAdapterPosition()).getMessage()));
                                    messageViewHolder.itemView.getContext().startActivity(intent);


                                }

                            }
                        });
                        builder.show();
                    }
                    else if (userMessagesList.get(messageViewHolder.getAdapterPosition()).getType().equals("text"))
                    {
                        CharSequence options[]= new CharSequence[]
                                {
                                        "Benden Sil",
                                        "İptal",
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Mesajı Sil");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if (i==0)
                                {
                                    deleteReceiveMessage(messageViewHolder.getAdapterPosition(), messageViewHolder);
                                    Intent intent =new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    intent.putExtra("url",userMessagesList.get(messageViewHolder.getAdapterPosition()).getMessage());
                                }


                            }
                        });
                        builder.show();
                    }
                    else if (userMessagesList.get(messageViewHolder.getAdapterPosition()).getType().equals("image"))
                    {
                        CharSequence options[]= new CharSequence[]
                                {
                                        "Benden Sil",
                                        "Resmi Görüntüle",
                                        "İptal",
                                };
                        AlertDialog.Builder builder=new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Mesajı Sil");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if (i==0)
                                {
                                    deleteReceiveMessage(messageViewHolder.getAdapterPosition(), messageViewHolder);
                                    Intent intent =new Intent(messageViewHolder.itemView.getContext(),MainActivity.class);
                                    intent.putExtra("url",userMessagesList.get(messageViewHolder.getAdapterPosition()).getMessage());

                                }
                                else if (i==1)
                                {
                                    Intent intent =new Intent(messageViewHolder.itemView.getContext(),ImageViewerActivity.class);
                                    intent.putExtra("url",userMessagesList.get(messageViewHolder.getAdapterPosition()).getMessage());
                                    messageViewHolder.itemView.getContext().startActivity(intent);
                                }

                            }
                        });
                        builder.show();
                    }

                }
            });
        }


    }



    @Override
    public int getItemCount()
    {
        return  userMessagesList.size();
    }




    private void deleteSentMessage(final int position,final  MessageViewHolder holder)
    {
        DatabaseReference rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {

                if (task.isSuccessful())
                {
                    Toast.makeText(holder.itemView.getContext(), "Başarıyla Silindi", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(holder.itemView.getContext(), "Bir Hata Oluştu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void deleteReceiveMessage(final int position,final  MessageViewHolder holder)
    {
        DatabaseReference rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {

                if (task.isSuccessful())
                {
                    Toast.makeText(holder.itemView.getContext(), "Başarıyla Silindi", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(holder.itemView.getContext(), "Bir Hata Oluştu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void deleteMessageForEveryOne(final int position,final  MessageViewHolder holder)
    {
        DatabaseReference rootRef= FirebaseDatabase.getInstance().getReference();
        rootRef.child("Messages")
                .child(userMessagesList.get(position).getTo())
                .child(userMessagesList.get(position).getFrom())
                .child(userMessagesList.get(position).getMessageID())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {

                if (task.isSuccessful())
                {
                    rootRef.child("Messages")
                            .child(userMessagesList.get(position).getFrom())
                            .child(userMessagesList.get(position).getTo())
                            .child(userMessagesList.get(position).getMessageID())
                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(holder.itemView.getContext(), "Başarıyla Silindi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else
                {
                    Toast.makeText(holder.itemView.getContext(), "Bir Hata Oluştu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}




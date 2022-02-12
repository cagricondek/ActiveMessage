package com.example.activemessage;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class GroupsFragment extends Fragment
{
private RecyclerView groupsRv;
private FirebaseAuth firebaseAuth;
private ArrayList<ModelGroupChatsList> groupChatsLists;
private AdapterGroupChatList adapterGroupChatList;

    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

     View view= inflater.inflate(R.layout.fragment_groups,container,false);
     groupsRv=view.findViewById(R.id.groupsRv);
     firebaseAuth  =FirebaseAuth.getInstance();
     loadGroupChatsList();






        return  view;
    }

    private void loadGroupChatsList() {
        groupChatsLists=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatsLists.size();
                for (DataSnapshot ds: snapshot.getChildren())
                {
                    if (ds.child("Participants").child(firebaseAuth.getUid()).exists())
                    {
                        ModelGroupChatsList model=ds.getValue(ModelGroupChatsList.class);
                        groupChatsLists.add(model);
                    }
                }
                adapterGroupChatList=new AdapterGroupChatList(getActivity(),groupChatsLists);
                groupsRv.setAdapter(adapterGroupChatList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchGroupChatsList(String query)  {
        groupChatsLists=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatsLists.size();
                for (DataSnapshot ds: snapshot.getChildren())
                {
                    if (ds.child("Participants").child(firebaseAuth.getUid()).exists())
                    {
                        if (ds.child("groupTitle").toString().toLowerCase().contains(query.toLowerCase()))
                        {
                            ModelGroupChatsList model=ds.getValue(ModelGroupChatsList.class);
                            groupChatsLists.add(model);
                        }
                    }
                }
                adapterGroupChatList=new AdapterGroupChatList(getActivity(),groupChatsLists);
                groupsRv.setAdapter(adapterGroupChatList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}

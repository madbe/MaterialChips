package edu.ben.sample;


import android.Manifest;
import android.app.Dialog;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.ben.materialchip.ChipsInput;
import edu.ben.materialchip.model.ChipInterface;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChipFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChipFragment extends DialogFragment {

    private static final String TAG = MainActivity.class.toString();

    @BindView(R.id.chips_input) ChipsInput mChipsInput;
    @BindView(R.id.validate) Button mValidateButton;
    @BindView(R.id.chip_list) TextView mChipListText;
    private List<ContactChip> mContactList;
    private ViewGroup mContainer;

    public static ChipFragment newInstance(int num) {
        ChipFragment f = new ChipFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chip, container, false);
        // butter knife
        ButterKnife.bind(this,view);
        mContainer = container;
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContactList = new ArrayList<>();

        // get contact list
        new RxPermissions(getActivity())
                .request(Manifest.permission.READ_CONTACTS)
                .subscribe(granted -> {
                    if(granted && mContactList.size() == 0)
                        getContactList();

                }, err -> {
                    Log.e(TAG, err.getMessage());
                    Toast.makeText(view.getContext(), "Error get contacts, see logs", Toast.LENGTH_LONG).show();
                });

        // chips listener
        mChipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                Log.e(TAG, "chip added, " + newSize);
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                Log.e(TAG, "chip removed, " + newSize);
            }

            @Override
            public void onTextChanged(CharSequence text) {
                Log.e(TAG, "text changed: " + text.toString());
            }
        });

        // show selected chips
        mValidateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String listString = "";
                for(ContactChip chip: (List<ContactChip>)  mChipsInput.getSelectedChipList()) {
                    listString += chip.getLabel() + " (" + (chip.getInfo() != null ? chip.getInfo(): "") + ")" + ", ";
                }
                Toast.makeText(getContext(),"Tosat",Toast.LENGTH_SHORT).show();
                mChipListText.setText(listString);
            }
        });
    }

    @Override
    public void onResume() {
        // Get existing layout params for the window
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        // Assign window properties to fill the parent
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        // Call super onResume after sizing
        super.onResume();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_chip,mContainer, false);
        dialogBuilder.setView(view);
        // butter knife
        ButterKnife.bind(this,view);

        initUI();
        return dialogBuilder.create();
    }

    private void initUI() {
        mContactList = new ArrayList<>();

        // get contact list
        new RxPermissions(getActivity())
                .request(Manifest.permission.READ_CONTACTS)
                .subscribe(granted -> {
                    if(granted && mContactList.size() == 0)
                        getContactList();

                }, err -> {
                    Log.e(TAG, err.getMessage());
                    Toast.makeText(getContext(), "Error get contacts, see logs", Toast.LENGTH_LONG).show();
                });

        // chips listener
        mChipsInput.addChipsListener(new ChipsInput.ChipsListener() {
            @Override
            public void onChipAdded(ChipInterface chip, int newSize) {
                Log.e(TAG, "chip added, " + newSize);
            }

            @Override
            public void onChipRemoved(ChipInterface chip, int newSize) {
                Log.e(TAG, "chip removed, " + newSize);
            }

            @Override
            public void onTextChanged(CharSequence text) {
                Log.e(TAG, "text changed: " + text.toString());
            }
        });

        // show selected chips
        mValidateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String listString = "";
                for(ContactChip chip: (List<ContactChip>)  mChipsInput.getSelectedChipList()) {
                    listString += chip.getLabel() + " (" + (chip.getInfo() != null ? chip.getInfo(): "") + ")" + ", ";
                }
                Toast.makeText(getContext(),"Tosat",Toast.LENGTH_SHORT).show();
                mChipListText.setText(listString);
            }
        });
    }

    /**
     * Get the contacts of the user and add each contact in the mContactList
     * And finally pass the mContactList to the mChipsInput
     */
    private void getContactList() {
        Cursor phones = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null,null,null, null);

        // loop over all contacts
        if(phones != null) {
            while (phones.moveToNext()) {
                // get contact info
                String phoneNumber = null;
                String id = phones.getString(phones.getColumnIndex(ContactsContract.Contacts._ID));
                String name = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String avatarUriString = phones.getString(phones.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                Uri avatarUri = null;
                if(avatarUriString != null)
                    avatarUri = Uri.parse(avatarUriString);

                // get phone number
                if (Integer.parseInt(phones.getString(phones.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor pCur = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { id }, null);

                    while (pCur != null && pCur.moveToNext()) {
                        phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    }

                    pCur.close();

                }

                ContactChip contactChip = new ContactChip(id, avatarUri, name, phoneNumber);
                // add contact to the list
                mContactList.add(contactChip);
            }
            phones.close();
        }

        // pass contact list to chips input
        mChipsInput.setFilterableList(mContactList);
    }
}

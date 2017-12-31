package edu.ben.sample;


import android.Manifest;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import edu.ben.materialchip.ChipsInput;
import edu.ben.materialchip.model.ChipInterface;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContactChipFragment extends Fragment {

    @BindView(R.id.chips_input)
    ChipsInput mChipsInput;
    @BindView(R.id.validate)
    Button mValidateButton;
    @BindView(R.id.chip_list)
    TextView mChipListText;
    private List<ContactChip> mContactList;

    public ContactChipFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_chip, container, false);
        // butter knife
        ButterKnife.bind(this,view);
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
                    if (pCur != null) {
                        while (pCur.moveToNext()) {
                            phoneNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        }

                        pCur.close();
                    }
                }

                ContactChip contactChip = new ContactChip(id, avatarUri, name, phoneNumber);
                // add contact to the list
                mContactList.add(contactChip);
            }
            phones.close();
        }

        String imageUrl = "http://alfalfastudio.com/wp-content/uploads/2013/08/9c30bcfc-0fab-44f9-9e5d-623279e2427b_Alex-Sandrine.jpg";
        ContactChip contactChip = new ContactChip(getRandomUUID(), imageUrl, "John due", "054-2345671");
        mContactList.add(contactChip);

        String imageUrl2 = "http://top43dprinting.com/wp-content/uploads/2014/07/Picture-of-person.png";
        ContactChip contactChip2 = new ContactChip(getRandomUUID(), imageUrl2, "Armando duo", "054-3456712");
        mContactList.add(contactChip2);

        String imageUrl3 = "http://www.hoteliermiddleeast.com/pictures/2017/ITP%20EVENTS/Hotelier%20Awards%202017/Shortlist/Concierge/CONCIERGE-HossamElAttia.JPG";
        ContactChip contactChip3 = new ContactChip(getRandomUUID(), imageUrl3, "ישראל ישראלי", "054-4567123");
        mContactList.add(contactChip3);

        String imageUrl4 = "https://www.google.co.il/imgres?imgurl=http%3A%2F%2Ftop43dprinting.com%2Fwp-content%2Fuploads%2F2014%2F07%2FPicture-of-person.png&imgrefurl=http%3A%2F%2Ftop43dprinting.com%2Fpicture-of-person%2F&docid=50Sq6W9FLGMPiM&tbnid=3sLgLDfpJhQuEM%3A&vet=10ahUKEwjX4OOh2_fXAhVC2qQKHVwTBQcQMwitASgDMAM..i&w=220&h=215&bih=919&biw=1680&q=person%20image&ved=0ahUKEwjX4OOh2_fXAhVC2qQKHVwTBQcQMwitASgDMAM&iact=mrc&uact=8#h=215&imgdii=-qo2KZ5Ney4o4M:&vet=10ahUKEwjX4OOh2_fXAhVC2qQKHVwTBQcQMwitASgDMAM..i&w=220";
        ContactChip contactChip4 = new ContactChip(getRandomUUID(), imageUrl4, "אמנון כהן", "054-5671234");
        mContactList.add(contactChip4);

        // pass contact list to chips input
        mChipsInput.setFilterableList(mContactList);
    }

    private String getRandomUUID() {
        // Creating a random UUID (Universally unique identifier).
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}

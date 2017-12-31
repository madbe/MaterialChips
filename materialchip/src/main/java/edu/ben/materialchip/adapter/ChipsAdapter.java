package edu.ben.materialchip.adapter;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v7.widget.ContentFrameLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import edu.ben.materialchip.ChipView;
import edu.ben.materialchip.ChipsInput;
import edu.ben.materialchip.model.ChipInterface;
import edu.ben.materialchip.util.RTLUtil;
import edu.ben.materialchip.util.ViewUtil;
import edu.ben.materialchip.views.ChipsInputEditText;
import edu.ben.materialchip.views.DetailedChipView;
import edu.ben.materialchip.views.FilterableListView;


public class ChipsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ChipsAdapter.class.toString();
    private static final int TYPE_EDIT_TEXT = 0;
    private static final int TYPE_ITEM = 1;
    private Context mContext;
    private ChipsInput mChipsInput;
    private List<ChipInterface> mChipList = new ArrayList<>();
    private String mHintLabel;
    private ChipsInputEditText mEditText;
    private RecyclerView mRecycler;
    private boolean mIsRTL;
    private int[] mFirstItemCoord;
    private int mFirstChipWidth;

    public ChipsAdapter(Context context, ChipsInput chipsInput, RecyclerView recycler) {
        mContext = context;
        mChipsInput = chipsInput;
        mRecycler = recycler;
        mHintLabel = mChipsInput.getHint();
        mEditText = mChipsInput.getEditText();
        isRtl();
        initEditText();
    }

    private void isRtl() {
        mIsRTL = RTLUtil.isRTL();
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ChipView chipView;

        ItemViewHolder(View view) {
            super(view);
            chipView = (ChipView) view;
        }
    }

    private class EditTextViewHolder extends RecyclerView.ViewHolder {

        private final EditText editText;

        EditTextViewHolder(View view) {
            super(view);
            editText = (EditText) view;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EDIT_TEXT)
            return new EditTextViewHolder(mEditText);
        else
            return new ItemViewHolder(mChipsInput.getChipView());

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        // edit text
        if (position == mChipList.size()) {
            if (mChipList.size() == 0)
                mEditText.setHint(mHintLabel);

            // auto fit edit text
            autofitEditText();
        }
        // chip
        else if (getItemCount() > 1) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.chipView.inflate(getItem(position));
            //get the first ChipItem coords and chip width
            if (position == 0) {
                mFirstItemCoord = getItemLocationInWindow(itemViewHolder.chipView);
                getItemWidth(itemViewHolder.chipView);
            }
            // handle click
            handleClickOnEditText(itemViewHolder.chipView, position);
        }
    }

    private void getItemWidth(final ChipView chipView) {
        chipView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // get chip width
                mFirstChipWidth = chipView.getWidth();
                // remove the listener:
                chipView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private int[] getItemLocationInWindow(final ChipView chipView) {
        final int[] coord = new int[2];
        chipView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // get chip location in window
                chipView.getLocationInWindow(coord);
                // remove the listener:
                chipView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }

        });

        return coord;
    }

    @Override
    public int getItemCount() {
        return mChipList.size() + 1;
    }

    private ChipInterface getItem(int position) {
        return mChipList.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mChipList.size())
            return TYPE_EDIT_TEXT;

        return TYPE_ITEM;
    }

    @Override
    public long getItemId(int position) {
        return mChipList.get(position).hashCode();
    }

    private void initEditText() {
        int paddingLeftRight = ViewUtil.dpToPx(4);
        int paddingTopBottom = ViewUtil.dpToPx(5);

//        mRecycler.setBackgroundResource(android.R.color.holo_blue_light);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        mEditText.setLayoutParams(lp);

        mEditText.setHint(mHintLabel);

        mEditText.setBackgroundResource(android.R.color.transparent); //transparent
        // prevent fullscreen on landscape
        mEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
        mEditText.setPrivateImeOptions("nm");
        // no suggestion
        mEditText.setInputType(InputType.TYPE_TEXT_VARIATION_FILTER | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

        // Check if text is RTL and Change layout direction
        if (mIsRTL) { //RTLUtil.checkRTL(mHintLabel)
            //RTL languages
            mEditText.setGravity(Gravity.END);
            mEditText.setPaddingRelative(paddingLeftRight, paddingTopBottom, paddingLeftRight, paddingTopBottom);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                mRecycler.setPaddingRelative(12, 24, 0, 24);
            }
        } else {
            //LTR languages
            mEditText.setGravity(Gravity.START);
            mEditText.setPadding(paddingLeftRight, paddingTopBottom, paddingLeftRight, paddingTopBottom);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                mEditText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                mRecycler.setPadding(12, 24, 0, 24);
            }
        }


        // handle back space
        mEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // backspace
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    // remove last chip
                    if (mChipList.size() > 0 && mEditText.getText().toString().length() == 0)
                        removeChip(mChipList.size() - 1);
                }
                return false;
            }
        });

        // text changed
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mChipsInput.onTextChanged(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void autofitEditText() {
        // min width of edit text = 50 dp
        ViewGroup.LayoutParams params = mEditText.getLayoutParams();
        params.width = ViewUtil.dpToPx(50);
        mEditText.setLayoutParams(params);

        // listen to change in the tree
        mEditText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                int recyclerRight, editLeft, editRight;
                // get right of recycler and left + right of edit text
                recyclerRight = mRecycler.getRight();
                editLeft = mEditText.getLeft();
                editRight = mEditText.getRight();

                // edit text will fill the space
                ViewGroup.LayoutParams params = mEditText.getLayoutParams();

                if (!mIsRTL) {
                    params.width = recyclerRight - editLeft - ViewUtil.dpToPx(4);
                } else {
                    params.width = (editRight - editLeft) + editLeft - ViewUtil.dpToPx(4);
                }

                mEditText.setLayoutParams(params);

                // request focus
                mEditText.requestFocus();

                // remove the listener:
                mEditText.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }

        });
    }

    private void handleClickOnEditText(ChipView chipView, final int position) {
        // delete chip
        chipView.setOnDeleteClicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeChip(position);
            }
        });

        // show detailed chip
        if (mChipsInput.isShowChipDetailed()) {
            chipView.setOnChipClicked(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // get chip position
                    int[] coord = new int[2];
                    v.getLocationInWindow(coord);

                    final DetailedChipView detailedChipView = mChipsInput.getDetailedChipView(getItem(position));
                    setDetailedChipViewPosition(detailedChipView, coord);

                    // delete button
                    detailedChipView.setOnDeleteClicked(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeChip(position);
                            detailedChipView.fadeOut();
                        }
                    });
                }
            });
        }
    }

    private void setDetailedChipViewPosition(DetailedChipView detailedChipView, int[] coord) {

        // window width
        ViewGroup rootView = (ViewGroup) mRecycler.getRootView();
        int windowWidth = ViewUtil.getWindowWidth(mContext);

        /*ContentFrameLayout cfl = (ContentFrameLayout) rootView.findViewById(android.R.id.content);
        int layoutWidth = cfl.getWidth();*/

        int width = mRecycler.getWidth();
        int layoutHeight = ((View) mRecycler.getParent()).getHeight();
        int layoutWidth = ((View) mRecycler.getParent()).getWidth();

        // chip size
        ContentFrameLayout.LayoutParams layoutParams = new ContentFrameLayout.LayoutParams(
                ViewUtil.dpToPx(300),
                ViewUtil.dpToPx(100));

        if (ViewUtil.getScreenOrientation(mContext) == Configuration.ORIENTATION_PORTRAIT) {
            if (!mIsRTL) {
                //LTR
                if ((coord[0] >= 0 && coord[0] < windowWidth)) {
                    layoutParams.leftMargin = mFirstItemCoord[0] - ViewUtil.dpToPx(8);
                    layoutParams.topMargin = coord[1] - ViewUtil.dpToPx(14);
                }

            } else {
                //RTL
                if ((coord[0] >= 0 && coord[0] < windowWidth)) {
                    layoutParams.rightMargin = ((windowWidth - mFirstItemCoord[0] - mFirstChipWidth) - ViewUtil.dpToPx(9));
                    layoutParams.topMargin = coord[1] - ViewUtil.dpToPx(14);
                }
            }
        } else {
            if (!mIsRTL) {
                //LTR
                if (coord[0] >= 0 && coord[0] < windowWidth) {
                    layoutParams.leftMargin = mFirstItemCoord[0] - ViewUtil.dpToPx(8);
                    layoutParams.topMargin = coord[1] - ViewUtil.dpToPx(14);
                }
            } else {
                //RTL
                if ((coord[0] >= 0 && coord[0] < windowWidth)) {
                    layoutParams.rightMargin = ((windowWidth - mFirstItemCoord[0] - mFirstChipWidth) + ViewUtil.dpToPx(34));
                    layoutParams.topMargin = coord[1] - ViewUtil.dpToPx(14);
                }
            }
        }

        // show view
//        cfl.addView(detailedChipView, layoutParams);
        rootView.addView(detailedChipView, layoutParams);
        detailedChipView.fadeIn();
    }

    public void setFilterableListView(FilterableListView filterableListView) {
        if (mEditText != null)
            mEditText.setFilterableListView(filterableListView);
    }

    public void addChip(ChipInterface chip) {
        if (!listContains(mChipList, chip)) {
            mChipList.add(chip);
            // notify listener
            mChipsInput.onChipAdded(chip, mChipList.size());
            // hide hint
            mEditText.setHint(null);
            // reset text
            mEditText.setText(null);
            // refresh data
            notifyItemInserted(mChipList.size());
        }
    }

    public void removeChip(ChipInterface chip) {
        int position = mChipList.indexOf(chip);
        mChipList.remove(position);
        // notify listener
        notifyItemRangeChanged(position, getItemCount());
        // if 0 chip
        if (mChipList.size() == 0)
            mEditText.setHint(mHintLabel);
        // refresh data
        notifyDataSetChanged();
    }

    public void removeChip(int position) {

        ChipInterface chip = mChipList.get(position);
        // remove contact
        mChipList.remove(position);

        // notify listener
        mChipsInput.onChipRemoved(chip, mChipList.size());

        // if 0 chip
        if (mChipList.size() == 0) mEditText.setHint(mHintLabel);

        // refresh data
        notifyDataSetChanged();
    }

    public void removeChipById(Object id) {
        for (Iterator<ChipInterface> iter = mChipList.listIterator(); iter.hasNext(); ) {
            ChipInterface chip = iter.next();
            if (chip.getId() != null && chip.getId().equals(id)) {
                // remove chip
                iter.remove();
                // notify listener
                mChipsInput.onChipRemoved(chip, mChipList.size());
            }
        }
        // if 0 chip
        if (mChipList.size() == 0)
            mEditText.setHint(mHintLabel);
        // refresh data
        notifyDataSetChanged();
    }

    public void removeChipByLabel(String label) {
        for (Iterator<ChipInterface> iter = mChipList.listIterator(); iter.hasNext(); ) {
            ChipInterface chip = iter.next();
            if (chip.getLabel().equals(label)) {
                // remove chip
                iter.remove();
                // notify listener
                mChipsInput.onChipRemoved(chip, mChipList.size());
            }
        }
        // if 0 chip
        if (mChipList.size() == 0)
            mEditText.setHint(mHintLabel);
        // refresh data
        notifyDataSetChanged();
    }

    public void removeChipByInfo(String info) {
        for (Iterator<ChipInterface> iter = mChipList.listIterator(); iter.hasNext(); ) {
            ChipInterface chip = iter.next();
            if (chip.getInfo() != null && chip.getInfo().equals(info)) {
                // remove chip
                iter.remove();
                // notify listener
                mChipsInput.onChipRemoved(chip, mChipList.size());
            }
        }
        // if 0 chip
        if (mChipList.size() == 0)
            mEditText.setHint(mHintLabel);
        // refresh data
        notifyDataSetChanged();
    }

    public List<ChipInterface> getChipList() {
        return mChipList;
    }

    private boolean listContains(List<ChipInterface> contactList, ChipInterface chip) {

        if (mChipsInput.getChipValidator() != null) {
            for (ChipInterface item : contactList) {
                if (mChipsInput.getChipValidator().areEquals(item, chip))
                    return true;
            }
        } else {
            for (ChipInterface item : contactList) {
                if (chip.getId() != null && chip.getId().equals(item.getId()))
                    return true;
                if (chip.getLabel().equals(item.getLabel()))
                    return true;
            }
        }

        return false;
    }
}

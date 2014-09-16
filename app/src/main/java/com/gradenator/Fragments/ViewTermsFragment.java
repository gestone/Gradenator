package com.gradenator.Fragments;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gradenator.Callbacks.OnTermChangedListener;
import com.gradenator.CustomViews.TermCard;
import com.gradenator.Internal.Session;
import com.gradenator.Internal.Term;
import com.gradenator.R;
import com.gradenator.Utilities.Util;

import java.util.ArrayList;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * Displays all stored terms to the user.
 */
public class ViewTermsFragment extends Fragment implements OnTermChangedListener {

    public static final String TAG = ViewTermsFragment.class.getSimpleName();

    private RelativeLayout mAddButtonLayout;
    private Button mAddButton;
    private CardListView mAllTerms;
    private List<Card> mAllCards;
    private String mSelectedTerm;
    private Resources mRes;

    public enum TermAction {

        ADD, EDIT, REMOVE

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_terms_frag, container, false);
        findAndSetViews(v);
        return v;
    }

    private void findAndSetViews(View v) {

        mRes = getActivity().getResources();
        mAddButtonLayout = (RelativeLayout) v.findViewById(R.id.add_button_header);
        mAddButton = (Button) v.findViewById(R.id.add_button);
        final OnTermChangedListener listener = this;
        mAddButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewTermDialog(listener, TermAction.ADD);
            }
        });
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewTermDialog(listener, TermAction.ADD);
            }
        });
        initListCardView(v);
    }

    /**
     * Creates a new term dialog
     * @param listener
     */
    private void createNewTermDialog(final OnTermChangedListener listener, final TermAction action) {
        final EditText termName = new EditText(getActivity());
        termName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT);
        termName.setHint(mRes.getString(R.string.term_title_hint));
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        termName.setBackgroundColor(Color.parseColor("#00000000"));
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        switch(action) {
            case ADD: {
                builder.setView(termName);
                builder.setTitle(mRes.getString(R.string.create_term_title));
                builder.setPositiveButton(mRes.getString(R.string.create_term),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String term = termName.getText().toString();
                                List<Term> currentTerms = Session.getInstance(getActivity()).getAllTerms();
                                currentTerms.add(0, new Term(term, System.currentTimeMillis(),
                                        Util.createRandomColor()));
                                Toast.makeText(getActivity(), term + " " + mRes.getString(R.string.term_success_msg), Toast.LENGTH_SHORT).show();
                                listener.onTermChanged(action);
                            }
                        }
                );
                break;
            }
            case EDIT: {
                builder.setView(termName);
                termName.setText(mSelectedTerm);
                termName.setSelection(mSelectedTerm.length());
                builder.setTitle(mRes.getString(R.string.edit_term_title));
                builder.setPositiveButton(mRes.getString(R.string
                        .save_edit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newTermName = termName.getText().toString();
                        if (!newTermName.equals(mSelectedTerm)) {
                            List<Term> allTerms = Session.getInstance(getActivity()).getAllTerms();
                            for (int i = 0; i < allTerms.size(); i++) {
                                CardHeader h = mAllCards.get(i).getCardHeader();
                                if (h.getTitle().equals(mSelectedTerm)) {
                                    allTerms.get(i).setTermName(newTermName);
                                    h.setTitle(newTermName);
                                    Card temp = mAllCards.remove(i);
                                    CardArrayAdapter c = (CardArrayAdapter) mAllTerms.getAdapter();
                                    c.notifyDataSetChanged();
                                    mAllCards.add(i, temp); // recreate card with new title
                                    c.notifyDataSetChanged();
                                    mSelectedTerm = "";
                                }
                            }
                        }
                    }
                });
                break;
            }
            case REMOVE: {
                String totalMsg = mRes.getString(R.string.confirm_msg_1) + " \"" +
                        mSelectedTerm + "\" " + mRes.getString(R.string.confirm_msg_2);
                builder.setTitle(mRes.getString(R.string.remove_term_title));
                builder.setMessage(totalMsg);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeCard();
                        Toast.makeText(getActivity(), mSelectedTerm + " " + mRes.getString(R.string
                                .successfully_deleted), Toast.LENGTH_SHORT).show();
                        mSelectedTerm = "";
                    }
                });
            }
        }
        final AlertDialog dialog = builder.create();
        termName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                            .SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        dialog.show();
    }

    private void removeCard() {
        for (int i = 0; i < mAllCards.size(); i++) {
            if (mAllCards.get(i).getCardHeader().getTitle().equals(mSelectedTerm)) {
                Session.getInstance(getActivity()).getAllTerms().remove(i);
                mAllCards.remove(i);
                ((CardArrayAdapter) mAllTerms.getAdapter()).notifyDataSetChanged();
            }
        }
    }

    /**
     * Initial initialization of the ListCardView.
     * @param v The View containing all other views.
     */
    private void initListCardView(View v) {
        mAllTerms = (CardListView) v.findViewById(R.id.all_terms);
        List<Term> terms = Session.getInstance(getActivity()).getAllTerms();
        mAllCards = new ArrayList<Card>();
        for (Term t : terms) {
            mAllCards.add(createNewCard(t));
        }
        CardArrayAdapter c = new CardArrayAdapter(getActivity(), mAllCards);
        mAllTerms.setAdapter(c);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void updateNewTermView(TermAction action) {
        CardArrayAdapter c = (CardArrayAdapter) mAllTerms.getAdapter();
        List<Term> allTerms = Session.getInstance(getActivity()).getAllTerms();
        if (!allTerms.isEmpty()) {
            switch(action) {
                case ADD: {
                    mAllCards.add(0, createNewCard(allTerms.get(0))); // get newly created term
                    break;
                }
                case EDIT: {

                    break;
                }
                case REMOVE: {
                    removeSelectedCard();
                    break;
                }
            }
            c.notifyDataSetChanged();
        }
    }

    private void removeSelectedCard() {
        for (int i = 0; i < mAllCards.size(); i++) {
            if (mAllCards.get(i).getCardHeader().getTitle().equals(mSelectedTerm)) {
                mAllCards.remove(i);
                mSelectedTerm = "";
                break;
            }
        }
    }


    private Card createNewCard(Term t) {
        Card termView = new TermCard(t, getActivity(), R.layout.custom_card);
        CardHeader termHeader = createCardHeader(t);
        termView.addCardHeader(termHeader);
        termView.addPartialOnClickListener(Card.CLICK_LISTENER_HEADER_VIEW, null);
        termView.addPartialOnClickListener(Card.CLICK_LISTENER_THUMBNAIL_VIEW, null);
        termView.addPartialOnClickListener(Card.CLICK_LISTENER_CONTENT_VIEW, new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Toast.makeText(getActivity(), "it nope", Toast.LENGTH_SHORT).show();
            }
        });
        return termView;
    }

    private CardHeader createCardHeader(Term t) {
        CardHeader termHeader = new CardHeader(getActivity());
        termHeader.setTitle(t.getTermName());
        termHeader.setButtonOverflowVisible(true);
        termHeader.setOtherButtonClickListener(null);
        final OnTermChangedListener listener = this;
        termHeader.setPopupMenuListener(new CardHeader.OnClickCardHeaderPopupMenuListener() {
            @Override
            public void onMenuItemClick(BaseCard card, MenuItem item) {
                Card c = (Card) card;
                mSelectedTerm = c.getCardHeader().getTitle();
                if (item.getTitle().toString().equals(getString(R.string.edit))) {
                    createNewTermDialog(listener ,TermAction.EDIT);
                } else if (item.getTitle().toString().equals(getString(R.string.remove))) {
                    createNewTermDialog(listener, TermAction.REMOVE);
                }
            }
        });

        termHeader.setPopupMenuPrepareListener(new CardHeader.OnPrepareCardHeaderPopupMenuListener() {
            @Override
            public boolean onPreparePopupMenu(BaseCard baseCard, PopupMenu popupMenu) {
                popupMenu.getMenu().add(mRes.getString(R.string.edit));
                popupMenu.getMenu().add(mRes.getString(R.string.remove));
                return true;
            }
        });

        return termHeader;
    }

    @Override
    public void onTermChanged(TermAction action) {
        updateNewTermView(action);
    }
}

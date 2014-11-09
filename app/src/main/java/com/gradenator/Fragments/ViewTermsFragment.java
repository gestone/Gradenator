package com.gradenator.Fragments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gradenator.Action;
import com.gradenator.Background.GradeUpdateReceiver;
import com.gradenator.Callbacks.OnEntryChangedListener;
import com.gradenator.CustomViews.CustomCardHeader;
import com.gradenator.CustomViews.TermCard;
import com.gradenator.Dialogs.GenericDialog;
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
public class ViewTermsFragment extends Fragment implements OnEntryChangedListener {

    public static final String TAG = ViewTermsFragment.class.getSimpleName();

    private RelativeLayout mAddButtonLayout;
    private Button mAddButton;
    private ImageView mImage;
    private TextView mMessage;
    private CardListView mAllTerms;
    private List<Card> mAllCards;
    private String mSelectedTerm;
    private Resources mRes;


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
        mImage = (ImageView) v.findViewById(R.id.info_image);
        mMessage = (TextView) v.findViewById(R.id.no_terms_msg);
        final OnEntryChangedListener listener = this;
        mAddButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewTermDialog(listener, Action.ADD);
            }
        });
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewTermDialog(listener, Action.ADD);
            }
        });
        initListCardView(v);
        hideOrShowNoTermsMsg();
    }

    private void hideOrShowNoTermsMsg() {
        if (Session.getInstance(getActivity()).hasTerms()) {
            Util.hideViews(mImage, mMessage);
        } else {
            Util.showViews(mImage, mMessage);
        }
    }

    /**
     * Creates a new term dialog
     * @param listener
     */
    private void createNewTermDialog(final OnEntryChangedListener listener, final Action action) {
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
                        new DialogInterface.OnClickListener() { // placeholder to be overriden
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }
                );
                final AlertDialog d = builder.create();
                setDialogListeners(d, termName, action);
                d.show();
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

                    }
                });
                final AlertDialog d = builder.create();
                setDialogListeners(d, termName, action);
                d.show();
                break;
            }
            case REMOVE: {
                String totalMsg = mRes.getString(R.string.confirm_msg_1) + " \"" +
                        mSelectedTerm.trim() + "\" " + mRes.getString(R.string.confirm_msg_2);
                builder.setTitle(mRes.getString(R.string.remove_term_title));
                builder.setMessage(totalMsg);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeCard();
                        hideOrShowNoTermsMsg();
                        Toast.makeText(getActivity(), mSelectedTerm + " " + mRes.getString(R.string
                                .successfully_deleted), Toast.LENGTH_SHORT).show();
                        mSelectedTerm = "";
                    }
                });
                builder.create().show();
            }
        }
    }

    private void setDialogListeners(final AlertDialog d, final EditText termName,
                                    final Action action) {
        final OnEntryChangedListener listener = this;
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button newTerm = d.getButton(AlertDialog.BUTTON_POSITIVE);
                newTerm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String term = termName.getText().toString().trim();
                        if (term.isEmpty()) {
                            String title = mRes.getString(R.string.empty_field_title);
                            String termError = mRes.getString(R.string
                                    .empty_term_error_msg);
                            Util.createErrorDialog(title, termError, getActivity());
                        } else if (isDuplicateTerm(term)) {
                            String title = getString(R.string.dup_term_title);
                            String termError = getString(R.string.dup_term_msg);
                            Util.createErrorDialog(title, termError, getActivity());
                        } else {
                            if (action == Action.ADD) {
                                setAddTermLogic(term);
                            } else if (action == Action.EDIT) {
                                setEditTermLogic(term);
                            }
                            listener.onEntryChanged(action);
                            hideOrShowNoTermsMsg();
                            d.dismiss();
                        }
                    }
                });
            }
        });
        termName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    d.getWindow().setSoftInputMode(WindowManager.LayoutParams
                            .SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
    }


    /**
     * Checks if a term with the same name already exists, if so, returns true otherwise, false.
     * @param termTitle
     * @return
     */
    private boolean isDuplicateTerm(String termTitle) {
        List<Term> allTerms = Session.getInstance(getActivity()).getAllTerms();
        for (Term t : allTerms) {
            if (t.getTermName().equals(termTitle)) {
                return true;
            }
        }
        return false;
    }


    private void setAddTermLogic(String termName) {
        List<Term> currentTerms = Session.getInstance(getActivity()).getAllTerms();
        currentTerms.add(0, new Term(termName));
        Toast.makeText(getActivity(), termName + " " + mRes.getString(R.string.term_success_msg), Toast.LENGTH_SHORT).show();
    }

    private void setEditTermLogic(String newTermName) {
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
        mAllTerms = (CardListView) v.findViewById(R.id.all_entries);
        List<Term> terms = Session.getInstance(getActivity()).getAllTerms();
        mAllCards = new ArrayList<Card>();
        for (Term t : terms) {
            mAllCards.add(createNewCard(t));
        }
        CardArrayAdapter c = new CardArrayAdapter(getActivity(), mAllCards);
        mAllTerms.setAdapter(c);
    }


    private void updateNewTermView(Action action) {
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
        Card termView = new TermCard(t, getActivity(), R.layout.custom_term_card);
        CustomCardHeader termHeader = createCardHeader(t);
        termView.addCardHeader(termHeader);
        setCardOnClickListeners(termView);
        return termView;
    }

    private void setCardOnClickListeners(Card termView) {
        termView.addPartialOnClickListener(Card.CLICK_LISTENER_HEADER_VIEW, null);
        termView.addPartialOnClickListener(Card.CLICK_LISTENER_THUMBNAIL_VIEW, null);
        termView.addPartialOnClickListener(Card.CLICK_LISTENER_CONTENT_VIEW, new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                Session s = Session.getInstance(getActivity());
                s.setCurrentTerm(s.findTerm(card.getCardHeader().getTitle()));
                Util.displayFragment(new ViewClassesFragment(), ViewClassesFragment.TAG, getActivity());
            }
        });
    }


    private CustomCardHeader createCardHeader(Term t) {
        CustomCardHeader termHeader = new CustomCardHeader(getActivity(), t.getTermName());
//        termHeader.setTitle(t.getTermName());
        termHeader.setButtonOverflowVisible(true);
        termHeader.setOtherButtonClickListener(null);
        final OnEntryChangedListener listener = this;
        termHeader.setPopupMenuListener(new CardHeader.OnClickCardHeaderPopupMenuListener() {
            @Override
            public void onMenuItemClick(BaseCard card, MenuItem item) {
                Card c = (Card) card;
                mSelectedTerm = c.getCardHeader().getTitle();
                if (item.getTitle().toString().equals(getString(R.string.edit))) {
                    createNewTermDialog(listener ,Action.EDIT);
                } else if (item.getTitle().toString().equals(getString(R.string.remove))) {
                    createNewTermDialog(listener, Action.REMOVE);
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
    public void onEntryChanged(Action action) {
        updateNewTermView(action);
    }
}

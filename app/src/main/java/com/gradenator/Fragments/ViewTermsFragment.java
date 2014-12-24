package com.gradenator.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import com.gradenator.Action;
import com.gradenator.CustomViews.GradenatorCardHeader;
import com.gradenator.CustomViews.TermCard;
import com.gradenator.Internal.Session;
import com.gradenator.Internal.Term;
import com.gradenator.R;
import com.gradenator.Utilities.Util;

import java.util.ArrayList;
import java.util.List;

import at.markushi.ui.CircleButton;
import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.base.BaseCard;
import it.gmariotti.cardslib.library.view.CardListView;

/**
 * Displays all stored terms to the user.
 */
public class ViewTermsFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = ViewTermsFragment.class.getSimpleName();

    private ImageView mImage;
    private TextView mMessage;
    private CardListView mAllTerms;
    private List<Card> mAllCards;
    private String mSelectedTerm;
    private Resources mRes;
    private CircleButton mAddButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_terms_frag, container, false);
        Util.changeActionBarTitle(getActivity(), getString(R.string.ab_all_terms));
        mRes = getActivity().getResources();
        mImage = (ImageView) v.findViewById(R.id.info_image);
        mMessage = (TextView) v.findViewById(R.id.no_terms_msg);
        mAddButton = (CircleButton) v.findViewById(R.id.add_term_button);
        mAddButton.setOnClickListener(this);
        initListCardView(v);
        hideOrShowNoTermsMsg();
        return v;
    }

    private void hideOrShowNoTermsMsg() {
        if (Session.getInstance(getActivity()).hasTerms()) {
            Util.hideViews(mImage, mMessage);
        } else {
            Util.showViews(mImage, mMessage);
        }
    }

    /**
     * Creates a new term dialog.
     */
    private void createNewTermDialog(final Action action) {
        final EditText termName = setupEditText();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog d;
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();}
        });
        if (action == Action.ADD || action == Action.EDIT) {
            builder.setView(termName);
            d = setupAddOrEdit(action, builder, termName);
            setDialogListeners(d, termName, action);
        } else { // action == Action.REMOVE
            setupRemoveDialog(builder);
            d = builder.create();
        }
        d.show();
        Util.changeDialogColor(d, getActivity());
    }

    // Sets up the static edit text that will be used for inputting a term name.
    private EditText setupEditText() {
        EditText termName = (EditText) getActivity().getLayoutInflater().inflate(R.layout
                .custom_edit_text, null);
        termName.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_CLASS_TEXT);
        termName.setHint(mRes.getString(R.string.term_title_hint));
        termName.setBackgroundColor(Color.parseColor("#00000000"));
        return termName;
    }


    // Sets up the add or edit action dialogs. A finished AlertDialog is returned to be displayed to
    // the user.
    private AlertDialog setupAddOrEdit(Action action, AlertDialog.Builder builder, EditText termName) {
        String title, positiveButton;
        if (action == Action.ADD) {
            title = mRes.getString(R.string.create_term_title);
            positiveButton = mRes.getString(R.string.create_term);
        } else {
            title = mRes.getString(R.string.edit_term_title);
            positiveButton = mRes.getString(R.string.save_edit);
            termName.setText(mSelectedTerm);
            termName.setSelection(mSelectedTerm.length());
        }
        builder.setTitle(title);
        builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
            @Override // to be overriden
            public void onClick(DialogInterface dialog, int which) {}});
        return builder.create();
    }

    // Sets up the remove dialog.
    private void setupRemoveDialog(AlertDialog.Builder builder) {
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
    }

    private void setDialogListeners(final AlertDialog d, final EditText termName,
                                    final Action action) {
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
                        } else if (isDuplicateTerm(term, action)) {
                            String title = getString(R.string.dup_term_title);
                            String termError = getString(R.string.dup_term_msg);
                            Util.createErrorDialog(title, termError, getActivity());
                        } else {
                            if (action == Action.ADD) {
                                setAddTermLogic(term);
                            } else if (action == Action.EDIT) {
                                setEditTermLogic(term);
                            }
                            updateNewTermView(action);
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
     * @param termTitle The term title that is to be considered.
     * @return          A boolean if the term with a same name exists, true if so, otherwise, false.
     */
    private boolean isDuplicateTerm(String termTitle, Action action) {
        List<String> allTerms = Session.getInstance(getActivity()).getAllTermNames();
        if (action == Action.EDIT && termTitle.equals(mSelectedTerm)) { // if the user doesn't edit,
                                                                        // it's not a duplicate
            return false;
        }
        return allTerms.contains(termTitle);
    }


    private void setAddTermLogic(String termName) {
        List<Term> currentTerms = Session.getInstance(getActivity()).getAllTerms();
        currentTerms.add(0, new Term(termName, Util.createRandomColor(getActivity())));
        Util.hideViews(mImage, mMessage);
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
        Toast.makeText(getActivity(), newTermName + " " + mRes.getString(R.string.term_edited_msg),
                Toast.LENGTH_SHORT).show();
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
     *
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
            switch (action) {
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
        GradenatorCardHeader termHeader = createCardHeader(t);
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
                String termTitle = card.getCardHeader().getTitle();
                s.setCurrentTerm(s.findTerm(termTitle));
                termTitle += " " + getString(R.string.ab_classes);
                Util.changeActionBarTitle(getActivity(), termTitle);
                Util.displayFragment(new ViewClassesFragment(), ViewClassesFragment.TAG, getActivity());
            }
        });
    }

    private GradenatorCardHeader createCardHeader(Term t) {
        GradenatorCardHeader termHeader = new GradenatorCardHeader(getActivity(), t.getTermName());
        termHeader.setButtonOverflowVisible(true);
        termHeader.setOtherButtonClickListener(null);
        termHeader.setPopupMenuListener(new CardHeader.OnClickCardHeaderPopupMenuListener() {
            @Override
            public void onMenuItemClick(BaseCard card, MenuItem item) {
                Card c = (Card) card;
                mSelectedTerm = c.getCardHeader().getTitle();
                if (item.getTitle().toString().equals(getString(R.string.edit))) {
                    createNewTermDialog(Action.EDIT);
                } else if (item.getTitle().toString().equals(getString(R.string.remove))) {
                    createNewTermDialog(Action.REMOVE);
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
    public void onClick(View v) {
        createNewTermDialog(Action.ADD);
    }
}

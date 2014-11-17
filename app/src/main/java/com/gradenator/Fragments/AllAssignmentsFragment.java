package com.gradenator.Fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gradenator.Action;
import com.gradenator.CustomViews.AssignmentCard;
import com.gradenator.CustomViews.CustomCardHeader;
import com.gradenator.CustomViews.FloatingAction;
import com.gradenator.Internal.Assignment;
import com.gradenator.Internal.Category;
import com.gradenator.Internal.Class;
import com.gradenator.Internal.Session;
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
 * Displays all assignments for a given class.
 */
public class  AllAssignmentsFragment extends Fragment implements View.OnClickListener {

    private String mSelectedAssignment;

    private Spinner mFilter;
    private Class mClass;
    private EditText mAssignmentTitleET;
    private EditText mEarnedPointsET;
    private EditText mMaxPointsET;
    private Spinner mAssignmentCategory;
    private CardListView mAllAssignments;
    private CardArrayAdapter mAssignmentAdapter;
    private List<Card> mAllCards;
    private ImageView mNoAssignmentsImage;
    private TextView mNoAssignmentsMessage;
    private CircleButton mAddAssignment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.all_assignments_frag, container, false);
        findViews(v);
        return v;
    }

    private void findViews(View v) {
        mClass = Session.getInstance(getActivity()).getCurrentClass();
        mFilter = (Spinner) v.findViewById(R.id.filter);
        mNoAssignmentsImage = (ImageView) v.findViewById(R.id.info_image);
        mNoAssignmentsMessage = (TextView) v.findViewById(R.id.no_terms_msg);
        mAddAssignment = (CircleButton) v.findViewById(R.id.add_assignment_btn);
        mAddAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAssignmentDialog(Action.ADD);
            }
        });
        populateSpinner(mFilter);
        mFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String category = (String) mFilter.getItemAtPosition(position);
                List<Assignment> allCategoryAssignments;
                if (!category.equals(getString(R.string.select_prompt))) {
                    allCategoryAssignments = mClass.getCategory(category).getAllAssignments();
                } else {
                    allCategoryAssignments = mClass.getAllAssignments();
                }
                List<Assignment> allAssignments = mClass.getAllAssignments();
                mAllCards.clear();
                for (int i = 0; i < allAssignments.size(); i++) {
                    Assignment a = allAssignments.get(i);
                    if (allCategoryAssignments.contains(a)) {
                        mAllCards.add(createNewCard(a));
                    }
                }
                mAssignmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        initCardView(v);
    }

    private void initCardView(View v) {
        mAllAssignments = (CardListView) v.findViewById(R.id.all_assignments_list);
        mAllCards = new ArrayList<Card>();
        List<Assignment> assignmentList = mClass.getAllAssignments();
        if (!assignmentList.isEmpty()){
            Util.hideViews(mNoAssignmentsImage, mNoAssignmentsMessage);
        }
        for (Assignment a : assignmentList) {
            mAllCards.add(createNewCard(a));
        }
        mAssignmentAdapter = new CardArrayAdapter(getActivity(), mAllCards);
        mAllAssignments.setAdapter(mAssignmentAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private List<String> getCategoryTitles() {
        List<String> categoryTitles = new ArrayList<String>();
        List<Category> categories = mClass.getAllCategories();
        for (Category c : categories) {
            categoryTitles.add(c.getTitle());
        }
        return categoryTitles;
    }

    private void populateSpinner(Spinner s) {
        List<String> allCategories = getCategoryTitles();
        allCategories.add(0, getString(R.string.select_prompt));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.filter_item, allCategories);
        s.setAdapter(dataAdapter);
    }

    private void createAssignmentDialog(final Action action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String msg = "";
        if (action == Action.ADD) {
            builder.setTitle(getString(R.string.create_assignment_header));
            msg = getString(R.string.create_assignment_header);
        } else if (action == Action.EDIT) {
            builder.setTitle(getString(R.string.edit_assignment_header));
            msg = getString(R.string.confirm_edit_msg);
        }
        LayoutInflater l = (LayoutInflater) getActivity().getSystemService(Context
                .LAYOUT_INFLATER_SERVICE);
        View v = l.inflate(R.layout.create_assignment, null);
        builder.setView(v);
        setupInflatedViews(v, action);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mSelectedAssignment = null;
            }
        });
        builder.setPositiveButton(msg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        final AlertDialog d = builder.create();
        setPositiveButtonListener(d, action);
        promptEditText(d);
        d.show();
    }

    private void promptEditText(final AlertDialog d) {
        mAssignmentTitleET.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    d.getWindow().setSoftInputMode(WindowManager.LayoutParams
                            .SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
    }

    private void setPositiveButtonListener(final AlertDialog d, final Action action) {
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button createAssignment = d.getButton(AlertDialog.BUTTON_POSITIVE);
                createAssignment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String assignmentTitle = mAssignmentTitleET.getText().toString();
                        String earnedPoints = mEarnedPointsET.getText().toString();
                        String maxPoints = mMaxPointsET.getText().toString();
                        String selectedCategory = mAssignmentCategory.getSelectedItem().toString();
                        if (fieldsAreValid(assignmentTitle, earnedPoints, maxPoints)) {
                            try {
                                Category c = mClass.getCategory(selectedCategory);
                                double parsedEarnedPoints = Double.parseDouble(earnedPoints);
                                double parsedMaxPoints = Double.parseDouble(maxPoints);
                                if (action == Action.ADD) {
                                    Assignment newAssignment = new Assignment(assignmentTitle.trim(),
                                            parsedEarnedPoints, parsedMaxPoints);
                                    c.addAssignment(newAssignment);
                                    Util.makeToast(getActivity(), getString(R.string.assignment_success_msg));
                                    Util.hideViews(mNoAssignmentsImage, mNoAssignmentsMessage);
                                    mAllCards.add(0, createNewCard(newAssignment));
                                    if (!mFilter.getSelectedItem().equals(getString(R.string
                                            .select_prompt)) && !mFilter.getSelectedItem()
                                            .equals(selectedCategory)) {
                                        mFilter.setSelection(getCategoryTitles().indexOf
                                                (selectedCategory) + 1);
                                    }
                                } else if (action == Action.EDIT) {
                                    Assignment toModify = mClass.findAssignment(mSelectedAssignment);
                                    Category oldCategory = mClass.getCategory(toModify);
                                    oldCategory.removeAssignment(toModify.getTitle()); // move category
                                    updateAssignmentCard(assignmentTitle);
                                    toModify.setTitle(assignmentTitle);
                                    toModify.setEarnedScore(parsedEarnedPoints);
                                    toModify.setMaxScore(parsedMaxPoints);
                                    Category newCategory = mClass.getCategory(selectedCategory);
                                    newCategory.addAssignment(toModify);
                                    Util.makeToast(getActivity(), getString(R.string.assignment_success_edit_msg));
                                    mSelectedAssignment = null;
                                }
                                mAssignmentAdapter.notifyDataSetChanged();
                                d.dismiss();
                            } catch (NumberFormatException e) {
                                Util.createErrorDialog(getString(R.string.points_invalid_format_title),
                                        getString(R.string.points_invalid_format_msg), getActivity());
                            }
                        }
                    }

                });
            }
        });
    }



    private void createRemoveDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        b.setTitle(getString(R.string.remove_assignment));
        b.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mSelectedAssignment = null;
            }
        });
        b.setPositiveButton(getString(R.string.remove_assignment_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mClass.getCategory(mClass.findAssignment(mSelectedAssignment)).removeAssignment
                        (mSelectedAssignment);
                removeAssignmentCard();
                mAssignmentAdapter.notifyDataSetChanged();
                if (mClass.getAllAssignments().isEmpty()) {
                    Util.showViews(mNoAssignmentsImage, mNoAssignmentsMessage);
                }
                Util.makeToast(getActivity(), mSelectedAssignment + " " + getString(R.string
                        .remove_assignment_success));
                mSelectedAssignment = null;
            }
        });
        String msg = getString(R.string.remove_assignment_1) + " \"" + mSelectedAssignment + "\" " +
                getString(R.string.remove_assignment_2);
        b.setMessage(msg);
        b.create().show();
    }

    private void removeAssignmentCard() {
        for (int i = 0; i < mAllCards.size(); i++) {
            AssignmentCard card = (AssignmentCard) mAllCards.get(i);
            if (card.getAssignment().getTitle().equals(mSelectedAssignment)) {
                mAllCards.remove(i);
            }
        }
    }


    private void setupInflatedViews(View v, Action action) {
        mAssignmentTitleET = (EditText) v.findViewById(R.id.assignment_title);
        mEarnedPointsET = (EditText) v.findViewById(R.id.earned_points);
        mMaxPointsET = (EditText) v.findViewById(R.id.max_points);
        mAssignmentCategory = (Spinner) v.findViewById(R.id.assignment_category);
        populateSpinner(mAssignmentCategory);
        if (action == Action.ADD) {
            mAssignmentCategory.setSelection(mFilter.getSelectedItemPosition());
        } else if (action == Action.EDIT) {
            Assignment selectedAssignment = mClass.findAssignment(mSelectedAssignment);
            mAssignmentTitleET.setText(selectedAssignment.getTitle());
            mEarnedPointsET.setText(createNumberString(selectedAssignment.getEarnedScore()));
            mMaxPointsET.setText(createNumberString(selectedAssignment.getMaxScore()));
            int pos = getCategoryTitles().indexOf(mClass.getCategory(selectedAssignment)
                    .getTitle()) + 1;
            mAssignmentCategory.setSelection(pos);
        }
    }

    private String createNumberString(double number) {
        String msg = "";
        if (number % 1 == 0) {
            return (int) number + msg;
        }
        return number + msg;
    }


    /**
     * Checks if all the fields are in a valid format to be converted to a category.
     *
     * @param assignmentTitle The assignment title that the user inputted.
     * @return A boolean representing whether or not all of the fields are in a
     * valid format.
     */
    private boolean fieldsAreValid(String assignmentTitle, String earnedPoints, String maxPoints) {
        if (areFieldsEmpty()) {
            Util.createErrorDialog(getString(R.string.empty_field_title),
                    getString(R.string.empty_field_error_msg), getActivity());
            return false;
        } else if (duplicateTitles(assignmentTitle)) {
            Util.createErrorDialog(getString(R.string.duplicate_title_header),
                    getString(R.string.duplicate_title_msg), getActivity());
            return false;
        } else if (earnedPoints.equals("0") && maxPoints.equals("0")) {
            Util.createErrorDialog(getString(R.string.zero_out_of_zero_title),
                    getString(R.string.zero_out_of_zero_msg), getActivity());
            return false;
        } else if (earnedPoints.length() > 4 || maxPoints.length() > 4) {
            Util.createErrorDialog(getString(R.string.too_many_digits),
                    getString(R.string.too_many_digits_msg), getActivity());
            return false;
        }
        return true;
    }


    /**
     * Checks if the fields are empty.
     *
     * @return A boolean representing whether or not the fields are empty.
     */
    private boolean areFieldsEmpty() {
        return mAssignmentTitleET.getText().toString().isEmpty() || mEarnedPointsET.getText()
                .toString().isEmpty() || mMaxPointsET.getText().toString().isEmpty() ||
                mAssignmentCategory.getSelectedItem().equals(getString(R.string.select_prompt));
    }

    /**
     * Checks to make sure there are no duplicate assignments.
     *
     * @param assignmentTitle
     * @return
     */
    private boolean duplicateTitles(String assignmentTitle) {
        List<Assignment> allAssignments = mClass.getAllAssignments();
        for (Assignment a : allAssignments) {
            if (a.getTitle().equals(assignmentTitle) && !a.getTitle().equals(mSelectedAssignment)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets the filter to the view that the user clicked from the categories.
     *
     * @param selected The category title of the selected view.
     */
    public void setSpinner(String selected) {
        mFilter.setSelection(getCategoryTitles().indexOf(selected) + 1);
    }

    private AssignmentCard createNewCard(Assignment a) {
        AssignmentCard assignmentView = new AssignmentCard(a, getActivity(), R.layout.custom_assignment_card);
        CustomCardHeader classHeader = createCardHeader(a);
        assignmentView.addCardHeader(classHeader);
        return assignmentView;
    }

    private CustomCardHeader createCardHeader(Assignment a) {
        CustomCardHeader assignmentHeader = new CustomCardHeader(getActivity(),
              a.getTitle());
        assignmentHeader.setButtonOverflowVisible(true);
        assignmentHeader.setOtherButtonClickListener(null);
        assignmentHeader.setPopupMenuListener(new CardHeader.OnClickCardHeaderPopupMenuListener() {
            @Override
            public void onMenuItemClick(BaseCard card, MenuItem item) {
                Card c = (Card) card;
                mSelectedAssignment = c.getCardHeader().getTitle();
                if (item.getTitle().toString().equals(getString(R.string.edit))) {
                    createAssignmentDialog(Action.EDIT);
                } else if (item.getTitle().toString().equals(getString(R.string.remove))) {
                    createRemoveDialog();
                }
            }
        });

        assignmentHeader.setPopupMenuPrepareListener(new CardHeader.OnPrepareCardHeaderPopupMenuListener() {
            @Override
            public boolean onPreparePopupMenu(BaseCard baseCard, PopupMenu popupMenu) {
                popupMenu.getMenu().add(getString(R.string.edit));
                popupMenu.getMenu().add(getString(R.string.remove));
                return true;
            }
        });

        return assignmentHeader;
    }

    private void updateAssignmentCard(String newTitle) {
        for (Card c : mAllCards) {
            AssignmentCard card = (AssignmentCard) c;
            if (card.getAssignment().getTitle().equals(mSelectedAssignment)) {
                card.updateCard();
                card.getCardHeader().setTitle(newTitle);
            }
        }
    }


    @Override
    public void onClick(View v) {
        createAssignmentDialog(Action.ADD);
    }
}

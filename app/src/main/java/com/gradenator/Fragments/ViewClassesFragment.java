package com.gradenator.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.gradenator.Action;
import com.gradenator.CustomViews.CreateCategoryAdapter;
import com.gradenator.CustomViews.ClassCard;
import com.gradenator.CustomViews.CustomCardHeader;
import com.gradenator.Internal.*;
import com.gradenator.Internal.Class;
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
 * Displays the classes for a given term to the user.
 */
public class ViewClassesFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = ViewClassesFragment.class.getSimpleName();

    private CardListView mAllClasses;
    private List<Card> mAllCards;
    private String mSelectedClass;
    private Resources mRes;
    private Term mCurrentTerm;
    private ImageView mInfoIcon;
    private TextView mNoClassMessage;
    private CircleButton mAddClassButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_classes_frag, container, false);
        mCurrentTerm = Session.getInstance(getActivity()).getCurrentTerm();
        findAndSetViews(v);
        return v;
    }

    /**
     * @param v The inflated view.
     */
    private void findAndSetViews(View v) {
        mRes = getActivity().getResources();
        mAllClasses = (CardListView) v.findViewById(R.id.all_entries);
        mInfoIcon = (ImageView) v.findViewById(R.id.info_image);
        mNoClassMessage = (TextView) v.findViewById(R.id.no_class_msg);
        mAddClassButton = (CircleButton) v.findViewById(R.id.add_class_btn);
        mAddClassButton.setOnClickListener(this);
        initListCardView();
    }

    private void initListCardView() {
        List<Class> allClasses = mCurrentTerm.getAllClasses();
        if (!allClasses.isEmpty()) {
            Util.hideViews(mInfoIcon, mNoClassMessage);
        }
        mAllCards = new ArrayList<Card>();
        for (Class c : allClasses) {
            mAllCards.add(createNewCard(c));
        }
        CardArrayAdapter c = new CardArrayAdapter(getActivity(), mAllCards);
        mAllClasses.setAdapter(c);
    }

    private Card createNewCard(Class curClass) {
        Card classView = new ClassCard(curClass, getActivity(), R.layout.custom_class_card);
        CustomCardHeader classHeader = createCardHeader(curClass);
        classView.addCardHeader(classHeader);
        setCardOnClickListeners(classView);
        return classView;
    }

    private CustomCardHeader createCardHeader(Class curClass) {
        CustomCardHeader termHeader = new CustomCardHeader(getActivity(),
                curClass.getClassName());
        termHeader.setButtonOverflowVisible(true);
        termHeader.setOtherButtonClickListener(null);
        termHeader.setPopupMenuListener(new CardHeader.OnClickCardHeaderPopupMenuListener() {
            @Override
            public void onMenuItemClick(BaseCard card, MenuItem item) {
                Card c = (Card) card;
                mSelectedClass = c.getCardHeader().getTitle();
                if (item.getTitle().toString().equals(getString(R.string.edit))) {
                    createDialog(Action.EDIT);
                } else if (item.getTitle().toString().equals(getString(R.string.remove))) {
                    createDialog(Action.REMOVE);
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

    private void createDialog(Action action) {
        if (action == Action.ADD || action == Action.EDIT) {
            createClassDialog(action);
        } else {
            createRemoveClassDialog();
        }
    }

    private void createRemoveClassDialog() {
        final ClassCard selectedClassCard = findSelectedClassCard();
        final Class selectedClass = getSelectedClass();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.remove_class_title));
        String msg = getString(R.string.remove_class_msg_1) + " \"" + selectedClass.getClassName()
                + "\" " + getString(R.string.remove_class_msg_2);
        builder.setMessage(msg);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAllCards.remove(selectedClassCard);
                mCurrentTerm.getAllClasses().remove(selectedClass);
                ((CardArrayAdapter) mAllClasses.getAdapter()).notifyDataSetChanged();
                Util.makeToast(getActivity(), selectedClass.getClassName() + " " + getString(R
                        .string.successfully_deleted));
                if (mCurrentTerm.getAllClasses().isEmpty()) {
                    Util.showViews(mInfoIcon, mNoClassMessage);
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void createClassDialog(Action action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.create_class, null, false);
        final EditText className = (EditText) v.findViewById(R.id.class_title_edit);
        final EditText unitCount = (EditText) v.findViewById(R.id.unit_count_edit);
        final ListView allCategories = (ListView) v.findViewById(R.id.all_categories);

        List<Category> listOfCategories = new ArrayList<Category>();
        if (action == Action.EDIT) {
            List<Category> copyCategories = getSelectedClass().getAllCategories();
            for (Category c : copyCategories) {
                listOfCategories.add(c);
            }
        }
        final CreateCategoryAdapter adapter = new CreateCategoryAdapter(getActivity(),
                listOfCategories);
        if (action == Action.EDIT) { // user wants to edit, populate the fields
            builder.setTitle(getString(R.string.edit_class_title));
            Class selectedClass = getSelectedClass();
            className.setText(selectedClass.getClassName());
            unitCount.setText(selectedClass.getUnitCount() + "");
        } else if (action == Action.ADD) {
            builder.setTitle(getString(R.string.create_class_header));
        }

        setCategoryButtonListeners(v, adapter, allCategories);
        allCategories.setAdapter(adapter);
        builder.setView(v);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        String createClass = "";
        if (action == Action.ADD) {
            createClass = getString(R.string.create_class_btn);
        } else if (action == Action.EDIT) {
            createClass = getString(R.string.update_class_btn);
        }
        builder.setPositiveButton(createClass, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        final AlertDialog d = builder.create();
        className.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    className.setSelection(className.getText().length());
                    className.setOnFocusChangeListener(null);
                }
            }
        });
        unitCount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    d.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                    unitCount.setSelection(unitCount.getText().length());
                }
            }
        });
        setCreateClassListener(d, className, unitCount, adapter, allCategories, action);
        d.show();
        Util.changeDialogColor(d, getActivity());
    }

    private void setCreateClassListener(final AlertDialog d, final EditText className,
                                        final EditText unitCount, final CreateCategoryAdapter adapter,
                                        final ListView allCategories, final Action action) {
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button createClassButton = d.getButton(AlertDialog.BUTTON_POSITIVE);
                createClassButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateCategories(allCategories, adapter);
                        String newClassName = className.getText().toString().trim();
                        String newUnitCount = unitCount.getText().toString();
                        if (newClassName.isEmpty()) { // user did not specify a class name
                            Util.createErrorDialog(getString(R.string.empty_field_title),
                                    getString(R.string.no_class_name), getActivity());
                        } else if (newUnitCount.isEmpty()) { // user did not specify a new unit count
                            Util.createErrorDialog(getString(R.string.empty_field_title),
                                    getString(R.string.no_unit_count), getActivity());
                        } else if (!checkFieldsCompleted(adapter)) { // all category fields are not complete
                            Util.createErrorDialog(getString(R.string.empty_field_title),
                                    getString(R.string.no_category_field),
                                    getActivity());
                        } else if (!checkWeights(adapter)) { // weights don't equal to 100
                            Util.createErrorDialog(getString(R.string.no_total_one_hundred_title),
                                    getString(R.string.no_total_one_hundred),
                                    getActivity());
                        } else if (checkDupCategories(adapter)) { // duplicate categories exist
                            Util.createErrorDialog(getString(R.string.duplicate_class_title),
                                    getString(R.string.duplicate_category_msg),
                                    getActivity());
                        } else if (checkDupClasses(newClassName, action)) {
                            // duplicate classes exist
                            Util.createErrorDialog(getString(R.string.duplicate_class_title),
                                    getString(R.string.duplicate_class_msg), getActivity());
                        } else if (newClassName.length() > 10) { // class title name is too long
                            Util.createErrorDialog(getString(R.string.class_title_too_long_title),
                                    getString(R.string.class_title_too_long_msg), getActivity());
                        } else {
                            if (action == Action.ADD) {
                                Class newClass = new Class(newClassName,
                                        Integer.parseInt(newUnitCount),
                                        Util.createRandomColor(), adapter.getCategoryList());
                                mCurrentTerm.addClass(newClass);
                                updateNewClassView(action);
                                String msg = getString(R.string.class_created_msg_1) + " \"" + newClassName +
                                        "\" " + getString(R.string.class_created_msg_2);
                                newClass.setAllCategories(adapter.getCategoryList());
                                Util.makeToast(getActivity(), msg);
                            } else if (action == Action.EDIT) {
                                Util.makeToast(getActivity(), getString(R.string.class_success_updated));
                                ClassCard selectedCard = findSelectedClassCard();
                                selectedCard.getCardHeader().setTitle(newClassName);
                                Class cur = selectedCard.getCorrespondingClass();
                                cur.setClassName(newClassName);
                                cur.setUnitCount(Integer.parseInt(newUnitCount));
                                cur.setAllCategories(adapter.getCategoryList());
                                CardArrayAdapter c = (CardArrayAdapter) mAllClasses.getAdapter();
                                getSelectedClass().setAllCategories(adapter.getCategoryList());
                                c.notifyDataSetChanged();
                            }
                            d.dismiss();
                        }
                    }
                });
            }
        });
    }

    private void setCardOnClickListeners(Card classView) {
        classView.addPartialOnClickListener(Card.CLICK_LISTENER_HEADER_VIEW, null);
        classView.addPartialOnClickListener(Card.CLICK_LISTENER_THUMBNAIL_VIEW, null);
        classView.addPartialOnClickListener(Card.CLICK_LISTENER_CONTENT_VIEW, new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                mSelectedClass = card.getCardHeader().getTitle();
                Session.getInstance(getActivity()).setCurrentClass(getSelectedClass());
                Util.changeActionBarTitle(getActivity(), getSelectedClass().getClassName());
                Util.displayFragment(new ViewSingleClassFragment(), ViewSingleClassFragment.TAG,
                        getActivity());
            }
        });
    }

    private void setCategoryButtonListeners(View v, final CreateCategoryAdapter adapter,
                                            final ListView allCategories) {
        final Button addCategory = (Button) v.findViewById(R.id.add_category);
        final Button removeCategory = (Button) v.findViewById(R.id.remove_category);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCategories(allCategories, adapter);
                Category newCategory = new Category();
                adapter.getCategoryList().add(newCategory);
                adapter.setRemoveOrAdd(true);
                adapter.notifyDataSetChanged();
            }
        });
        removeCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Category> categories = adapter.getCategoryList();
                if (categories.size() > 1) {
                    categories.remove(categories.size() - 1);
                    adapter.setRemoveOrAdd(false);
                    adapter.notifyDataSetChanged();
                    updateCategories(allCategories, adapter);
                } else if (categories.size() == 1) {
                    Toast.makeText(getActivity(), getString(R.string.category_error),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateCategories(final ListView allCategories, final CreateCategoryAdapter adapter) {
        for (int i = 0; i < adapter.getCount(); i++) {
            View row = adapter.getViewByPosition(i, allCategories);
            EditText title = (EditText) row.findViewById(R.id.category_name);
            EditText weight = (EditText) row.findViewById(R.id.category_weight);
            String categoryWeight = weight.getText().toString();
            if (!categoryWeight.isEmpty()) {
                adapter.getCategoryList().get(i).setWeight(Integer
                        .parseInt(categoryWeight));
            }
            adapter.getCategoryList().get(i).setTitle(title.getText().toString());
        }
    }

    private boolean checkFieldsCompleted(CreateCategoryAdapter adapter) {
        List<Category> allCategories = adapter.getCategoryList();
        for (Category c : allCategories) { // check if all fields are filled out
            if (c.getTitle().isEmpty() || c.getWeight() == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the weights all add up to 100.
     *
     * @return A boolean stating whether the weights equal 100 or not.
     */
    private boolean checkWeights(CreateCategoryAdapter adapter) {
        List<Category> allCategories = adapter.getCategoryList();
        int total = 0;
        for (Category c : allCategories) {
            total += c.getWeight();
        }
        return total == 100;
    }

    private boolean checkDupCategories(CreateCategoryAdapter adapter) {
        List<Category> allCategories = adapter.getCategoryList();
        for (int i = 0; i < allCategories.size(); i++) {
            Category cur = allCategories.get(i);
            for (int j = 0; j < allCategories.size(); j++) {
                if (cur.getTitle().equals(allCategories.get(j).getTitle()) && i != j) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkDupClasses(String newClassName, Action action) {
        Class selectedClass = null;
        if (action == Action.EDIT) {
            selectedClass = getSelectedClass();
        }
        List<Class> allClasses = mCurrentTerm.getAllClasses();
        for (Class c : allClasses) {
            if (c.getClassName().equals(newClassName) && !c.equals(selectedClass)) {
                return true;
            }
        }
        return false;
    }

    private void updateNewClassView(Action action) {
        CardArrayAdapter c = (CardArrayAdapter) mAllClasses.getAdapter();
        List<Class> allClasses = mCurrentTerm.getAllClasses();
        if (!allClasses.isEmpty()) {
            switch (action) {
                case ADD: {
                    mAllCards.add(createNewCard(allClasses.get(allClasses.size() - 1)));
                    Util.hideViews(mInfoIcon, mNoClassMessage);
                    break;
                }
                case REMOVE: {
                    break;
                }
            }
            c.notifyDataSetChanged();
        }
    }


    private ClassCard findSelectedClassCard() {
        List<Class> allClasses = mCurrentTerm.getAllClasses();
        for (int i = 0; i < allClasses.size(); i++) {
            CardHeader h = mAllCards.get(i).getCardHeader();
            CustomCardHeader custom = (CustomCardHeader) h;
            if (custom.getTitle().equals(mSelectedClass)) {
                return (ClassCard) mAllCards.get(i);
            }
        }
        return null;
    }


    private Class getSelectedClass() {
        List<Class> allClasses = mCurrentTerm.getAllClasses();
        for (Class c : allClasses) {
            if (c.getClassName().equals(mSelectedClass)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        createClassDialog(Action.ADD);
    }

}

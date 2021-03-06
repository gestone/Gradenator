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
import com.gradenator.CustomViews.GradeClassHeader;
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

    private void findAndSetViews(View v) {
        mRes = getActivity().getResources();
        mAllClasses = (CardListView) v.findViewById(R.id.all_entries);
        mInfoIcon = (ImageView) v.findViewById(R.id.info_image);
        mNoClassMessage = (TextView) v.findViewById(R.id.no_class_msg);
        mAddClassButton = (CircleButton) v.findViewById(R.id.add_class_btn);
        mAddClassButton.setOnClickListener(this);
        initListCardView();
    }

    @Override
    public void onClick(View v) {
        createClassDialog(Action.ADD);
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
        GradeClassHeader classHeader = createCardHeader(curClass);
        classView.addCardHeader(classHeader);
        setCardOnClickListeners(classView);
        return classView;
    }

    private GradeClassHeader createCardHeader(Class curClass) {
        GradeClassHeader termHeader = new GradeClassHeader(getActivity(),
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
        builder.create();
        Util.changeDialogColor(builder.show(), getActivity());
    }

    private void createClassDialog(Action action) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View layout = setupClassDialogLayout(action, builder);
        builder.setView(layout);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        String createClass = (action == Action.ADD) ? getString(R.string.create_class_btn) :
                getString(R.string.update_class_btn);
        builder.setPositiveButton(createClass, new DialogInterface.OnClickListener() {
            @Override // to be override again
            public void onClick(DialogInterface dialog, int which) {}
        });
        final AlertDialog d = builder.create();
        setCreateClassListener(d, (EditText) layout.getTag(R.string.class_name),
                (EditText) layout.getTag(R.string.unit_count),
                (CreateCategoryAdapter) layout.getTag(R.string.adapter),
                (ListView) layout.getTag(R.string.all_categories), action);
        d.show();
        Util.changeDialogColor(d, getActivity());
    }

    private View setupClassDialogLayout(Action action, AlertDialog.Builder builder) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.create_class, null, false);
        final EditText className = (EditText) v.findViewById(R.id.class_title_edit);
        final EditText unitCount = (EditText) v.findViewById(R.id.unit_count_edit);
        final ListView allCategories = (ListView) v.findViewById(R.id.all_categories);
        List<Category> listOfCategories = new ArrayList<Category>();
        if (action == Action.EDIT) { // user wants to edit, populate the fields
            Class selectedClass = getSelectedClass();
            className.setText(selectedClass.getClassName());
            unitCount.setText(selectedClass.getUnitCount() + "");
            List<Category> copyCategories = getSelectedClass().getAllCategories();
            for (Category c : copyCategories) {
                listOfCategories.add(c);
            }
        }
        final CreateCategoryAdapter adapter = new CreateCategoryAdapter(getActivity(),
                listOfCategories);
        String title = (action == Action.ADD) ? getString(R.string.create_class_header) :
                getString(R.string.edit_class_title);
        builder.setTitle(title);
        setCategoryButtonListeners(v, adapter, allCategories);
        allCategories.setAdapter(adapter);
        setTagsForViews(className, unitCount, allCategories, adapter, v);
        return v;
    }

    /**
     * Sets tags for views so they can be later retrieved for
     */
    private void setTagsForViews(EditText className, EditText unitCount, ListView allCategories,
                                CreateCategoryAdapter adapter, View v) {
        v.setTag(R.string.class_name, className);
        v.setTag(R.string.unit_count, unitCount);
        v.setTag(R.string.all_categories, allCategories);
        v.setTag(R.string.adapter, adapter);
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
                        String newClassName = className.getText().toString().trim();
                        String newUnitCount = unitCount.getText().toString();
                        List<Category> updatedCategories = updatedCategoriesInQuestion(allCategories, adapter);
                        if (validateFields(newClassName, newUnitCount, updatedCategories, action)) {
                            // if the validation is successful...
                            addOrEditClass(allCategories, adapter, action, newClassName, newUnitCount);
                            d.dismiss();
                        }
                    }
                });
            }
        });
    }

    /**
     * Validates the fields and ensures that fields that the user has entered are acceptable
     * parameters to input. If the inputted fields pass all of the tests,
     * the new class the user has specified will be added or the existing class will be edited.
     */
    private boolean validateFields(String newClassName, String newUnitCount,
                                List<Category> updatedCategories, Action action) {
        if (newClassName.isEmpty()) { // user did not specify a class name
            Util.createErrorDialog(getString(R.string.empty_field_title),
                    getString(R.string.no_class_name), getActivity());
        } else if (newUnitCount.isEmpty()) { // user did not specify a new unit count
            Util.createErrorDialog(getString(R.string.empty_field_title),
                    getString(R.string.no_unit_count), getActivity());
        } else if (!checkFieldsCompleted(updatedCategories)) { // cat fields inc
            Util.createErrorDialog(getString(R.string.empty_field_title),
                    getString(R.string.no_category_field),
                    getActivity());
        } else if (!checkWeights(updatedCategories)) { // weights don't equal to 100
            Util.createErrorDialog(getString(R.string.no_total_one_hundred_title),
                    getString(R.string.no_total_one_hundred),
                    getActivity());
        } else if (checkDupCategories(updatedCategories)) { // duplicate categories exist
            Util.createErrorDialog(getString(R.string.duplicate_class_title),
                    getString(R.string.duplicate_category_msg),
                    getActivity());
        } else if (checkDupClasses(newClassName, action)) {  // duplicate classes exist
            Util.createErrorDialog(getString(R.string.duplicate_class_title),
                    getString(R.string.duplicate_class_msg), getActivity());
        } else if (newClassName.length() > 10) { // class title name is too long
            Util.createErrorDialog(getString(R.string.class_title_too_long_title),
                    getString(R.string.class_title_too_long_msg), getActivity());
        } else {
            return true;
        }
        return false;
    }

    /**
     * Called only if all the fields are properly validated.
     */
    private void addOrEditClass(ListView allCategories, CreateCategoryAdapter adapter,
                                Action action, String newClassName, String newUnitCount) {
        updateCategories(allCategories, adapter);
        if (action == Action.ADD) {
            Class newClass = new Class(newClassName,
                    Integer.parseInt(newUnitCount),
                    Util.createRandomColor(getActivity()), adapter.getCategoryList());
            createNewClass(newClass, action, newClassName, adapter);
        } else if (action == Action.EDIT) {
            editExistingClass(newClassName, newUnitCount, adapter);
        }
    }


    private void createNewClass(Class newClass, Action action, String newClassName,
                                CreateCategoryAdapter adapter) {
        mCurrentTerm.addClass(newClass);
        updateNewClassView(action);
        String msg = getString(R.string.class_created_msg_1) + " \"" + newClassName +
                "\" " + getString(R.string.class_created_msg_2);
        newClass.setAllCategories(adapter.getCategoryList());
        Util.makeToast(getActivity(), msg);
    }

    private void editExistingClass(String newClassName, String newUnitCount,
                                   CreateCategoryAdapter adapter) {
        Util.makeToast(getActivity(), getString(R.string.class_success_updated));
        ClassCard selectedCard = findSelectedClassCard();
        selectedCard.getCardHeader().setTitle(newClassName);
        Class cur = selectedCard.getCorrespondingClass();
        cur.setClassName(newClassName);
        cur.setUnitCount(Integer.parseInt(newUnitCount));
        cur.setAllCategories(adapter.getCategoryList());
        CardArrayAdapter c = (CardArrayAdapter) mAllClasses.getAdapter();
        cur.setAllCategories(adapter.getCategoryList());
        c.notifyDataSetChanged();
    }

    private void setCardOnClickListeners(Card classView) {
        classView.addPartialOnClickListener(Card.CLICK_LISTENER_HEADER_VIEW, null);
        classView.addPartialOnClickListener(Card.CLICK_LISTENER_THUMBNAIL_VIEW, null);
        classView.addPartialOnClickListener(Card.CLICK_LISTENER_CONTENT_VIEW, new Card.OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
                mSelectedClass = card.getCardHeader().getTitle();
                Session.getInstance(getActivity()).setCurrentClass(getSelectedClass());
                Util.changeActionBarTitle(getActivity(), getSelectedClass().getClassName() + " "
                        + getString(R.string.ab_overview));
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
                addNewCategory(allCategories, adapter);
            }
        });
        removeCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCategory(allCategories, adapter);
            }
        });
    }

    private void addNewCategory(ListView allCategories, CreateCategoryAdapter adapter) {
        updateCategories(allCategories, adapter);
        Category newCategory = new Category(getActivity());
        List<Category> categoryList = adapter.getCategoryList();
        categoryList.add(newCategory);
        adapter.notifyDataSetChanged();
        allCategories.smoothScrollToPosition(categoryList.size() - 1);
    }

    private void removeCategory(ListView allCategories, CreateCategoryAdapter adapter) {
        List<Category> categories = adapter.getCategoryList();
        if (categories.size() > 1) {
            categories.remove(categories.size() - 1);
            adapter.notifyDataSetChanged();
            adapter.notifyDataSetInvalidated();
            updateCategories(allCategories, adapter);
            allCategories.smoothScrollToPosition(categories.size() - 1);
        } else if (categories.size() == 1) {
            Toast.makeText(getActivity(), getString(R.string.category_error),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCategories(ListView allCategories, CreateCategoryAdapter adapter) {
        for (int i = 0; i < adapter.getCount(); i++) {
            View row = adapter.getViewByPosition(i, allCategories);
            EditText title = (EditText) row.findViewById(R.id.category_name);
            EditText weight = (EditText) row.findViewById(R.id.category_weight);
            String categoryWeight = weight.getText().toString();
            if (!categoryWeight.isEmpty()) {
                adapter.getCategoryList().get(i).setWeight(Double
                        .parseDouble(categoryWeight));
            }
            adapter.getCategoryList().get(i).setTitle(title.getText().toString());
        }
    }

    private List<Category> updatedCategoriesInQuestion(ListView allCategories,
                                                       CreateCategoryAdapter adapter) {
        List<Category> updatedCategories = new ArrayList<Category>();
        for (int i = 0; i < adapter.getCount(); i++) {
            View row = adapter.getViewByPosition(i, allCategories);
            EditText title = (EditText) row.findViewById(R.id.category_name);
            EditText weight = (EditText) row.findViewById(R.id.category_weight);
            String categoryTitle = title.getText().toString();
            String categoryWeight = weight.getText().toString();
            updatedCategories.add(new Category(categoryTitle, Double.parseDouble(categoryWeight)));
        }
        return updatedCategories;
    }

    private boolean checkFieldsCompleted(List<Category> allCategories) {
        for (Category c : allCategories) { // check if all fields are filled out
            if (c.getTitle().isEmpty() || c.getWeight() == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean checkWeights(List<Category> allCategories) {
        double total = 0;
        for (Category c : allCategories) {
            total += c.getWeight();
        }
        return total == 100.0;
    }

    private boolean checkDupCategories(List<Category> allCategories) {
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
            GradeClassHeader custom = (GradeClassHeader) h;
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
}
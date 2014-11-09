package com.gradenator.Fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gradenator.Internal.Class;
import com.gradenator.Internal.DataPoint;
import com.gradenator.Internal.Session;
import com.gradenator.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.util.List;

/**
 * Displays a graph of the user's overall percentage throughout the term.
 */
public class ViewGraphFragment extends Fragment {

    private LinearLayout mGraphView;
    private GraphView mGraph;
    private Class mClass;
    private TextView mGraphTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.graph_layout, container, false);
        mClass = Session.getInstance(getActivity()).getCurrentClass();
        setupGraph(v);
        return v;
    }

    private void setupGraph(View v) {
        mGraphTitle = (TextView) v.findViewById(R.id.graph_title);
        mGraphView = (LinearLayout) v.findViewById(R.id.graph);
        mGraph = new LineGraphView(getActivity(), "");
        mGraph.setManualMaxY(true);
        mGraph.setManualYAxisBounds(100, 0);
        List<DataPoint> allDataPoints = mClass.getDataPoints();
        if (allDataPoints.size() > 6) { // display only the newest 6 points
            allDataPoints = allDataPoints.subList(allDataPoints.size() - 6, allDataPoints.size());
        }
        String title = mClass.getClassName() + " " + getResources().getString(R.string
                .graph_title_msg);
        mGraphTitle.setText(title);
        GraphView.GraphViewData[] percentageData = createDataPoints(allDataPoints);
        GraphViewSeries percentage = new GraphViewSeries("Total Percentage",
                new GraphViewSeries.GraphViewSeriesStyle(Color.rgb(0, 0, 0), 3), percentageData);
        mGraph.setHorizontalLabels(createHorizontalLabels(allDataPoints));
//        mGraph.getGraphViewStyle().setNumHorizontalLabels(allDataPoints.size());
//        mGraph.getGraphViewStyle().setNumVerticalLabels(20);
        mGraph.addSeries(percentage);
        mGraph.getGraphViewStyle().useTextColorFromTheme(getActivity());
        mGraphView.addView(mGraph);
    }

    private String[] createHorizontalLabels(List<DataPoint> allDataPoints) {
        String[] labels = new String[allDataPoints.size()];
        for (int i = 0; i < allDataPoints.size(); i++) {
            labels[i] = allDataPoints.get(i).getDate(); // for testing
        }
        return labels;
    }

    private GraphView.GraphViewData[] createDataPoints(List<DataPoint> allDataPoints) {
        GraphView.GraphViewData[] percentageData = new GraphView.GraphViewData[allDataPoints.size
                ()];
        for (int i = 0; i < allDataPoints.size(); i++) {
            percentageData[i] = new GraphView.GraphViewData(i, allDataPoints.get(i).getPercentage
                    ());
        }
        return percentageData;
    }

}

package in.ivinnovations.healthtracker.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeightChartView extends View {

    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private List<Float> weights = new ArrayList<>();
    private List<String> labels = new ArrayList<>();

    public WeightChartView(
            Context context,
            AttributeSet attrs
    ) {
        super(context, attrs);

        linePaint.setStrokeWidth(5f);
        linePaint.setStyle(Paint.Style.STROKE);

        pointPaint.setStyle(Paint.Style.FILL);

        textPaint.setTextSize(28f);

        gridPaint.setStrokeWidth(1f);
        gridPaint.setStyle(Paint.Style.STROKE);
    }

    public void setData(
            List<Float> weights,
            List<String> labels
    ) {

        this.weights = weights;
        this.labels = labels;

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        if (weights == null || weights.isEmpty()) {
            return;
        }

        float left = 70f;
        float right = getWidth() - 25f;
        float top = 25f;
        float bottom = getHeight() - 50f;

        float min = weights.get(0);
        float max = weights.get(0);

        for (Float weight : weights) {

            if (weight < min) {
                min = weight;
            }

            if (weight > max) {
                max = weight;
            }
        }

        if (min == max) {
            min -= 1;
            max += 1;
        }

        float range = max - min;

        // Add some breathing room.
        min -= range * 0.15f;
        max += range * 0.15f;

        // Horizontal grid lines.

        for (int i = 0; i <= 4; i++) {

            float y =
                    top +
                            (bottom - top)
                                    * i / 4f;

            canvas.drawLine(
                    left,
                    y,
                    right,
                    y,
                    gridPaint
            );
        }

        Path path = new Path();

        float step =
                weights.size() == 1
                        ? 0
                        : (right - left)
                          / (weights.size() - 1);

        for (int i = 0; i < weights.size(); i++) {

            float weight = weights.get(i);

            float x =
                    weights.size() == 1
                            ? (left + right) / 2
                            : left + i * step;

            float y =
                    bottom -
                            ((weight - min)
                                    / (max - min))
                                    * (bottom - top);

            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }

            canvas.drawCircle(
                    x,
                    y,
                    8f,
                    pointPaint
            );

            if (i < labels.size()) {

                canvas.drawText(
                        labels.get(i),
                        x - 12,
                        bottom + 35,
                        textPaint
                );
            }
        }

        canvas.drawPath(path, linePaint);

        // Show min/max scale.

        textPaint.setTextSize(24f);

        canvas.drawText(
                String.format(
                        Locale.getDefault(),
                        "%.1f",
                        max
                ),
                8,
                top + 8,
                textPaint
        );

        canvas.drawText(
                String.format(
                        Locale.getDefault(),
                        "%.1f",
                        min
                ),
                8,
                bottom,
                textPaint
        );
    }
}
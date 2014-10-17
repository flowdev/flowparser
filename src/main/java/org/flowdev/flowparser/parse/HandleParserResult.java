package org.flowdev.flowparser.parse;

import org.flowdev.base.data.Feedback;
import org.flowdev.base.data.NoConfig;
import org.flowdev.base.op.FilterOp;
import org.flowdev.flowparser.data.FlowFile;
import org.flowdev.flowparser.data.MainData;

import java.io.PrintStream;
import java.util.List;


public class HandleParserResult extends FilterOp<MainData, NoConfig> {

    @Override
    protected void filter(MainData data) {
        Feedback feedback = data.parserData().result().feedback();

        if (feedback == null || feedback.errors().isEmpty()) {
            if (feedback != null) {
                outputFeedback(feedback.infos(), "Information", System.out);
                outputFeedback(feedback.warnings(), "Warnings", System.out);
                outputFeedback(feedback.errors(), "ERRORS", System.out);
            }
            data.flowFile((FlowFile) data.parserData().result().value());
            outPort.send(data);
        } else {
            outputFeedback(feedback.infos(), "Information", System.err);
            outputFeedback(feedback.warnings(), "Warnings", System.err);
            outputFeedback(feedback.errors(), "ERRORS", System.err);
            sendError(new Exception("Errors occured while parsing. Please see the log for details."));
        }
    }

    private static void outputFeedback(List<String> feedback, String title, PrintStream oStream) {
        if (!feedback.isEmpty()) {
            oStream.println(title + ":");
            for (String entry : feedback) {
                oStream.println(entry);
            }
        }
    }
}

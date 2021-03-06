package framsticks;

import cecj.interaction.TestResult;
import cecj.problem.TestBasedProblem;
import ec.EvolutionState;
import ec.Individual;

public class FramsticksCoevolutionaryProblem extends TestBasedProblem {

	@Override
	public TestResult test(EvolutionState state, Individual candidate, Individual test) {

		if (!(candidate instanceof FramsticksIndividual) || !(test instanceof FramsticksIndividual)) {
			state.output
					.error("The individuals for this problem should be of class FramsticksIndividual.\n");
		}

		FramsticksIndividual framstickCandidate = (FramsticksIndividual) candidate;
		FramsticksIndividual framstickTest = (FramsticksIndividual) test;

		// TODO: Prepare for multithreading
		String fileName = "coevolutionary_evaluation_thread.gen";

		FramsticksUtils utils = FramsticksUtils.getInstance(state);
		return utils.coevolutionaryEvaluate(framstickCandidate.genotype, framstickTest.genotype,
				fileName);
	}

}

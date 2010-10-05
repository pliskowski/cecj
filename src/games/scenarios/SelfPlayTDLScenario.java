package games.scenarios;

import ec.util.MersenneTwisterFast;
import games.Board;
import games.BoardGame;
import games.GameMove;
import games.Player;

import java.util.List;

public class SelfPlayTDLScenario extends GameScenario {

	private double prob;
	private Player player;
	private double learningRate;

	private double lambda;
	private double[][] traces;

	public SelfPlayTDLScenario(MersenneTwisterFast random, Player player, double prob,
			double learningRate) {
		this(random, player, prob, learningRate, 0);
	}

	public SelfPlayTDLScenario(MersenneTwisterFast random, Player player, double prob,
			double learningRate, double lambda) {
		super(random);

		this.prob = prob;
		this.player = player;
		this.lambda = lambda;
		this.learningRate = learningRate;
	}

	@Override
	public int play(BoardGame game) {
		int boardSize = game.getBoard().getSize();
		traces = new double[boardSize + 1][boardSize + 1];

		while (!game.endOfGame()) {
			List<? extends GameMove> moves = game.findMoves();
			if (!moves.isEmpty()) {
				if (random.nextBoolean(prob)) {
					game.makeMove(moves.get(random.nextInt(moves.size())));
				} else {
					GameMove bestMove = chooseBestMove(game, player, moves);
					Board previousBoard = game.getBoard().clone();
					game.makeMove(bestMove);
					updateEvaluationFunction(previousBoard, game);
				}
			} else {
				game.pass();
			}
		}

		return game.getOutcome();
	}

	private void updateEvaluationFunction(Board previousBoard, BoardGame game) {
		double evalBefore = Math.tanh(player.evaluate(previousBoard));
		double error;

		if (game.endOfGame()) {
			int result;
			if (game.getOutcome() > 0) {
				result = 1;
			} else if (game.getOutcome() < 0) {
				result = -1;
			} else {
				result = 0;
			}
			error = result - evalBefore;
		} else {
			double evalAfter = Math.tanh(player.evaluate(game.getBoard()));
			error = evalAfter - evalBefore;
		}

		player.TDLUpdate(previousBoard, learningRate * error, traces, lambda);
	}
}

package seecontrol;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import gamestate.GlobalConstants;
import gamestate.Move;
import seecontrol.AttackSetData.AttackSetType;
import gamestate.GlobalConstants.PieceType;
import gamestate.GlobalConstants.Player;
import gamestate.GlobalConstants.Square;

public class AttackSetDataTest {
	@Test
	void testGettersSetters() {
		int asData = 0;
		assertEquals(0, AttackSetData.getAttackSetType(asData));
		assertEquals(0, AttackSetData.getPieceType(asData));
		assertEquals(0, AttackSetData.getSquare(asData));
		assertEquals(false, AttackSetData.getIsBatteryWithPawnPush(asData));
		assertEquals(0, AttackSetData.getSunkenCost(asData));
		assertEquals(0, AttackSetData.getOpponentSunkenCost(asData));
		assertEquals(Player.WHITE, AttackSetData.getPlayer(asData));

		asData = AttackSetData.setAttackSetType(asData, AttackSetType.PAWN_PUSH);
		assertEquals(AttackSetType.PAWN_PUSH, AttackSetData.getAttackSetType(asData));
		assertEquals(0, AttackSetData.getPieceType(asData));
		assertEquals(0, AttackSetData.getSquare(asData));
		assertEquals(false, AttackSetData.getIsBatteryWithPawnPush(asData));
		assertEquals(0, AttackSetData.getSunkenCost(asData));
		assertEquals(0, AttackSetData.getOpponentSunkenCost(asData));
		assertEquals(Player.WHITE, AttackSetData.getPlayer(asData));

		asData = 0;
		asData = AttackSetData.setPieceType(asData, PieceType.KING);
		assertEquals(0, AttackSetData.getAttackSetType(asData));
		assertEquals(PieceType.KING, AttackSetData.getPieceType(asData));
		assertEquals(0, AttackSetData.getSquare(asData));
		assertEquals(false, AttackSetData.getIsBatteryWithPawnPush(asData));
		assertEquals(0, AttackSetData.getSunkenCost(asData));
		assertEquals(0, AttackSetData.getOpponentSunkenCost(asData));
		assertEquals(Player.WHITE, AttackSetData.getPlayer(asData));

		asData = 0;
		asData = AttackSetData.setSquare(asData, Square.H8);
		assertEquals(0, AttackSetData.getAttackSetType(asData));
		assertEquals(0, AttackSetData.getPieceType(asData));
		assertEquals(Square.H8, AttackSetData.getSquare(asData));
		assertEquals(false, AttackSetData.getIsBatteryWithPawnPush(asData));
		assertEquals(0, AttackSetData.getSunkenCost(asData));
		assertEquals(0, AttackSetData.getOpponentSunkenCost(asData));
		assertEquals(Player.WHITE, AttackSetData.getPlayer(asData));

		asData = 0;
		asData = AttackSetData.setIsBatteryWithPawnPush(asData, true);
		assertEquals(0, AttackSetData.getAttackSetType(asData));
		assertEquals(0, AttackSetData.getPieceType(asData));
		assertEquals(0, AttackSetData.getSquare(asData));
		assertEquals(true, AttackSetData.getIsBatteryWithPawnPush(asData));
		assertEquals(0, AttackSetData.getSunkenCost(asData));
		assertEquals(0, AttackSetData.getOpponentSunkenCost(asData));
		assertEquals(Player.WHITE, AttackSetData.getPlayer(asData));

		asData = 0;
		asData = AttackSetData.setSunkenCost(asData, 255);
		assertEquals(0, AttackSetData.getAttackSetType(asData));
		assertEquals(0, AttackSetData.getPieceType(asData));
		assertEquals(0, AttackSetData.getSquare(asData));
		assertEquals(false, AttackSetData.getIsBatteryWithPawnPush(asData));
		assertEquals(255, AttackSetData.getSunkenCost(asData));
		assertEquals(0, AttackSetData.getOpponentSunkenCost(asData));
		assertEquals(Player.WHITE, AttackSetData.getPlayer(asData));
		
		asData = 0;
		asData = AttackSetData.setOppontntSunkenCost(asData, 255);
		assertEquals(0, AttackSetData.getAttackSetType(asData));
		assertEquals(0, AttackSetData.getPieceType(asData));
		assertEquals(0, AttackSetData.getSquare(asData));
		assertEquals(false, AttackSetData.getIsBatteryWithPawnPush(asData));
		assertEquals(0, AttackSetData.getSunkenCost(asData));
		assertEquals(255, AttackSetData.getOpponentSunkenCost(asData));
		assertEquals(Player.WHITE, AttackSetData.getPlayer(asData));
		
		asData = 0;
		asData = AttackSetData.setPlayer(asData, Player.BLACK);
		assertEquals(0, AttackSetData.getAttackSetType(asData));
		assertEquals(0, AttackSetData.getPieceType(asData));
		assertEquals(0, AttackSetData.getSquare(asData));
		assertEquals(false, AttackSetData.getIsBatteryWithPawnPush(asData));
		assertEquals(0, AttackSetData.getSunkenCost(asData));
		assertEquals(0, AttackSetData.getOpponentSunkenCost(asData));
		assertEquals(Player.BLACK, AttackSetData.getPlayer(asData));
	}
}

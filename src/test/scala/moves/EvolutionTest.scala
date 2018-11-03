package moves

import org.scalatest.FlatSpec
import senjinn.parsers.MoveParsing
import senjinn.parsers.BoardParsing
import senjinn.base.{loadResource}
import senjinn.board.{ BoardState, HashCache }
import senjinn.moves.{ ChessMove, StandardMove, CastleMove, EnpassantMove, PromotionMove }

/**
 */
class EvolutionTest extends FlatSpec with MoveParsing with BoardParsing {
  
  val testpkg: Package = getClass.getPackage
  type TestCaseArgs = (ChessMove, BoardState, BoardState)
  
  testCaseIterator foreach { testcase => 
    val (move, start, end) = testcase
    val startcpy = start.copy
  }
  

  def testCaseIterator: Iterator[TestCaseArgs] = {
    (0 until 40).iterator
      .map(n => ('0' * n.toString.length - 1) + n.toString)
      .map(name => loadResource(testpkg, name))
      .map(parseTestFile(_))
  }

  private def parseTestFile(lines: Seq[String]): TestCaseArgs = {
    import HashCache.{ size => hcachesze }
    val start = parseBoard(lines.slice(1, 10))
    val end = parseBoard(lines.slice(10, 19))
    val nhcache = end.hashCache.copyCache
    nhcache((end.hashCache.currIndex - 1) % hcachesze) = start.computeHash
    val end2 = new BoardState(end.pieceLocations, HashCache(nhcache, end.hashCache.currIndex),
      end.castleStatus, end.piecesDeveloped, end.clock, end.enpassant, end.active)

    (parseMove(lines.head), start, end2)
  }

  private def parseMove(encoded: String): ChessMove = {
    import senjinn.parsers.ChessRegex.{ castleZone => czregex }
    val (src, target) = "[a-h][1-8]".r.findAllIn(encoded).toVector.splitAt(1)
    encoded.toLowerCase.head match {
      case 's' => StandardMove(src(0), target(0))
      case 'e' => EnpassantMove(src(0), target(0))
      case 'c' => CastleMove(czregex.findFirstIn(encoded).get)
      case 'p' => PromotionMove(src(0), target(0), "[nbrq] ".r.findFirstIn(encoded).get)
      case _   => throw new RuntimeException
    }
  }
}
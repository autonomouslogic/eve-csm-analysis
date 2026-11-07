# CSM [(${analysis.csmNumber})] Ballots

All distinct ballots from the [CSM [(${analysis.csmNumber})] analysis](/csm[(${analysis.csmNumber})]), as in the official
`votes.blt` file, with candidate numbers replaced by names.
Each row is a group of identical ballots, with the number of ballots and the candidates in rank order.
There are [(${#numbers.formatInteger(ballotRows, 1, 'COMMA')})] distinct ballots, ordered as in the source file.

[Source data](/data/csm[(${analysis.csmNumber})]-ballots.csv)

Filter by candidate:
<input type="search" id="ballots-filter" class="ballots-filter" placeholder="Candidate name">

<p id="ballots-status"></p>

<table id="ballots-table" class="ballots-table" data-src="/data/csm[(${analysis.csmNumber})]-ballots.csv">
  <thead></thead>
  <tbody></tbody>
</table>

<script src="/javascripts/ballots.js"></script>

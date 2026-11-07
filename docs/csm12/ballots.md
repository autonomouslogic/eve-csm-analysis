# CSM 12 Ballots

All distinct ballots from the [CSM 12 analysis](/csm12), as in the official
`votes.blt` file, with candidate numbers replaced by names.
Each row is a group of identical ballots, with the number of ballots and the candidates in rank order.
There are 11,474 distinct ballots, ordered as in the source file.

[Source data](/data/csm12-ballots.csv)

Filter by candidate:
<input type="search" id="ballots-filter" class="ballots-filter" placeholder="Candidate name">

<p id="ballots-status"></p>

<table id="ballots-table" class="ballots-table" data-src="/data/csm12-ballots.csv">
  <thead></thead>
  <tbody></tbody>
</table>

<script src="/javascripts/ballots.js"></script>

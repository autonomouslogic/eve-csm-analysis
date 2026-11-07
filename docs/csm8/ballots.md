# CSM 8 Ballots

All distinct ballots from the [CSM 8 analysis](/csm8), as in the official
`votes.blt` file, with candidate numbers replaced by names.
Each row is a group of identical ballots, with the number of ballots and the candidates in rank order.
There are 20,378 distinct ballots, ordered as in the source file.

[Source data](/data/csm8-ballots.csv)

Filter by candidate:
<input type="search" id="ballots-filter" class="ballots-filter" placeholder="Candidate name">

<p id="ballots-status"></p>

<table id="ballots-table" class="ballots-table" data-src="/data/csm8-ballots.csv">
  <thead></thead>
  <tbody></tbody>
</table>

<script src="/javascripts/ballots.js"></script>

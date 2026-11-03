# Glossary

## Ballots Without a Winner

This is the number of ballots that did not rank any of the elected candidates.
Every candidate on these ballots was either eliminated or had withdrawn from the election.
The percentage is relative to the total number of votes cast.
Candidates picked are not elected by vote, so they do not count as winners here.

## Candidate Statistics

These measure how each candidate was ranked on the ballots, before any votes were transferred.
Ballots are weighted by their vote count, the same as everywhere else on the site.
For the overlap between candidates, see [Jaccard Index](#jaccard-index).

### Ballots

This is the number of ballots ranking the candidate at any position.
The percentage is relative to the total number of votes cast.
A candidate who was ranked on nearly every ballot has a high number here, even if they were rarely ranked first.

### First Choice

This is the number of ballots ranking the candidate first.
The percentage is relative to the total number of votes cast.
Unlike [Votes at Election](#votes-at-election), this is the first preference only and does not include votes transferred
from other candidates.

### Mean Rank

This is the average rank the candidate was given, across only the ballots they appear on.
A lower number means the candidate was ranked higher on average.
Ballots not ranking the candidate are ignored, so a low mean on a small number of ballots can be misleading.
Compare it with [Ballots](#ballots).

### Median Rank

This is the middle rank the candidate was given, across only the ballots they appear on.
When the number of ballots is even, the two middle ranks are averaged, which can give a value ending in .5.
The median is less affected by a few very high or very low ranks than the mean.

## Jaccard Index

The Jaccard index compares two sets by how much they overlap. It is the size of the intersection divided by the size of
the union, giving 0 for no overlap and 1 for identical sets.

On this site each candidate is a set of ballots: the ballots ranking them at any position. For two candidates, the
index is the ballots ranking both divided by the ballots ranking either. Ballots are weighted by vote count.

For the general definition and worked examples, see [Jaccard index on Wikipedia](https://en.wikipedia.org/wiki/Jaccard_index).

This is shown as the candidate overlap heatmap on each election page.
A value of 1 means the two candidates were on exactly the same ballots.
A value of 0 means no ballot ranked both.
Values in between show partial overlap, so a high value suggests voters who liked one candidate also tended to rank the other.
The diagonal is always 1, since a candidate is compared with themselves.

## Least Significant Rank

The least significant rank is calculated by taking all votes cast and removing the higher rank votes to see
if that removal would affect the outcome of the election.
For instance, in an election with a least significant rank of 3, it means that if you removed all rank 4-10 from all ballots
and reran the election, the same candidates would be selected.

## Votes at Election

This is the number of votes assigned to the candidate at the time they were selected.
In STV, each round has a "quota" which defines the number of votes required to win.
Votes beyond this quota are transferred to the ballots' next choices.
Therefore, a 1-to-1 comparison between these numbers and the total number of votes cast cannot be made.
It's merely a measure of the relative popularity of the candidate.

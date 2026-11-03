# Glossary

## Ballots Without a Winner

This is the number of ballots that did not rank any of the elected candidates.
Every candidate on these ballots was either eliminated or had withdrawn from the election.
The percentage is relative to the total number of votes cast.
Candidates picked by CCP are not elected by vote, so they do not count as winners here.

## Candidate Statistics

Overall statistics about each candidate before any votes are transferred.

### Ballots

The number of ballots ranking the candidate at any position.
The percentage is relative to the total number of ballots.

### First Choice

The number of ballots ranking the candidate as the first choice.
The percentage is relative to the total number of ballots.

### Mean Rank

The [arithmatic mean](https://en.wikipedia.org/wiki/Arithmetic_mean) of all ranks the candidate was given across ballots
they appear on.
A lower number means the candidate was ranked higher on average.

### Median Rank

The [median](https://en.wikipedia.org/wiki/Median) of all ranks the candidate was given across ballots they appear on.
It is the rank which divides the number of ballots in two.
Half the ballows are below this rank and the other half are above.

## Jaccard Index

The Jaccard index compares the similarity of two candidates across all the ballots.
It is defined as the ballots ranking both candidates divided by the ballots ranking either candidate.
A value of 1.0 means all ballots containing A also contain B.
A value of 0.0 means of no ballots containing A also contain B.
A value of 0.5 means half the ballots containing A also contain B.
And so on.
See [Jaccard index on Wikipedia](https://en.wikipedia.org/wiki/Jaccard_index) for more information.

## Least Significant Rank

The lowest rank which has an effect on the election.
For instance, an election with a least significant rank of 3 could delete all rank 4 and above choices from all ballots
and the final result would remain unchanged.

## Votes at Election

This is the number of votes assigned to the candidate at the time they were selected.
In STV, each round has a "quota" which defines the number of votes required to win.
Votes beyond this quota are transferred to the ballots' next choices.
Therefore, a 1-to-1 comparison between these numbers and the total number of votes cast cannot be made.
It's merely a measure of the relative popularity of the candidate.

Feature: Registracija korisnika
    Dodatni kontekst za ovaj feature

    Scenario: Ispravna registracija korisnika
        Given korisnik ne postoji
        When korisnik se registruje sa ispravnim podacima
        Then korisnik je uspesno registrovan

    Scenario: Ispravna registracija korisnika sa Cucumber parametrima
        Given korisnik "pera" ne postoji
        When korisnik "pera" se registruje sa imenom "Pera Peric" i lozinkom "123456"
        Then korisnik "pera" je uspesno registrovan

    Scenario: Ispravna registracija drugog korisnika sa Cucumber parametrima
        Given korisnik "zika" ne postoji
        When korisnik "zika" se registruje sa imenom "Zika Zivkovic" i lozinkom "111111"
        Then korisnik "zika" je uspesno registrovan

    Scenario: Registracija korisnika putem API-a
        Given putem API-a proveravamo da li korisnik "pera" vec registrovan
        When korisnik "pera" poziva API za registraciju sa imenom i prezimenom "Pera Peric" i lozinkom "123456"
        Then korisnik provera putem API-a da li je korisnik "pera" uspesno registrovan
